package makjust.service.impl;

import com.google.common.collect.Maps;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.dao.MCConfigFileDao;
import makjust.dao.MCServerDao;
import makjust.dao.MCSettingDao;
import makjust.dao.impl.MCConfigFileDaoImpl;
import makjust.dao.impl.MCServerDaoImpl;
import makjust.dao.impl.MCSettingDaoImpl;
import makjust.bean.MCServer;
import makjust.bean.MCServerConfigFile;
import makjust.bean.MCSetting;
import makjust.serverCore.ProcessServer;
import makjust.service.MCServerService;
import makjust.utils.DBPool;
import makjust.utils.EnvOptions;
import makjust.utils.SysConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MCServerServiceImpl implements MCServerService {
    private final String DIR = SysConfig.getCorePath("/");
    // 默认CMD为Windows 若需要为Linux还需修改
    private final String CMD = "cmd";
    private ProcessServer Server;
    private final MCServerDao mcServerDao = new MCServerDaoImpl();
    private final MCSettingDao mcSettingDao = new MCSettingDaoImpl();
    private final MCConfigFileDao mcConfigFileDao = new MCConfigFileDaoImpl();


    @Override
    public void setServerSetting(MCServer mcServer, Handler<AsyncResult<JsonObject>> resultHandler) {
        mcServerDao
                .updateMCServerInfo(mcServer)
                .onSuccess(rows -> {
                    if (rows.rowCount() > 0) {
                        resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "成功更新" + rows.rowCount() + "条记录")));
                    } else {
                        resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg", "更新失败")));
                    }
                })
                .onFailure(e -> {
                    e.printStackTrace();
                    resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "更新失败").put("code", 500)));
                });
    }

    @Override
    public void getServerInfo(MCServer server, Handler<AsyncResult<JsonObject>> resultHandler) {
        mcServerDao.getServerById(server).onSuccess(rows -> resultHandler.handle(Future.succeededFuture(DBPool.camelMapping(rows)))).onFailure(throwable -> {
            throwable.printStackTrace();
            resultHandler.handle(Future.failedFuture(throwable));
        });
    }

    @Override
    public void setCoreSetting(MCSetting setting, Handler<AsyncResult<JsonObject>> resultHandler) {
        mcSettingDao.updateSetting(setting)
                .onSuccess(rows -> {
                    if (rows.rowCount() > 0) {
                        resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "成功更新" + rows.rowCount() + "条记录")));
                    } else {
                        resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg", "更新失败")));
                    }
                })
                .onFailure(e -> {
                    resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg", "更新失败").put("data", e.toString())));
                    e.printStackTrace();

                });
    }

    @Override
    public void getSetting(Vertx vertx, MCSetting mcSetting, Handler<AsyncResult<JsonObject>> resultHandler) {
        mcSettingDao.getSettingById(mcSetting.getServerId()).onSuccess(ar -> resultHandler.handle(Future.succeededFuture(DBPool.camelMapping(ar))));
    }

    @Override
    public void getConfigFileList(Vertx vertx, MCServer mcServer, Handler<AsyncResult<JsonObject>> resultHandler) {
        mcServerDao.getServerLocationById(mcServer).onSuccess(rows -> {
            MCServer server = DBPool.objectMapping(MCServer.class, rows);
            if (server != null && server.getLocation() != null) {
                List<Object> configFiles = new ArrayList<>();
                Future<RowSet<Row>> getFileName = mcConfigFileDao.getAllConfigFile();
                Future<List<String>> scanFile = vertx.fileSystem().readDir(SysConfig.getCorePath(server.getLocation()), "\\w*.(yml|properties|txt|xml)");
                CompositeFuture.all(getFileName, scanFile).onComplete(ar -> {
                    List<MCServerConfigFile> mcServerConfigFiles = DBPool.ListObjectMapping(MCServerConfigFile.class, ar.result().resultAt(0));
                    List<String> fileList = ar.result().resultAt(1);
                    fileList.replaceAll(f -> f.substring(f.lastIndexOf("\\") + 1));
                    if (mcServerConfigFiles != null) {
                        mcServerConfigFiles.forEach(v -> {
                            if (fileList.contains(v.getFileName())) {
                                configFiles.add(JsonObject.mapFrom(v));
                            }
                        });
                    }
                    resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", configFiles)));
                }).onFailure(Throwable::printStackTrace);
            }
        }).onFailure(throwable -> {
            throwable.printStackTrace();
            resultHandler.handle(Future.failedFuture(throwable));
        });
    }

    @Override
    public void readConfigFile(Vertx vertx, MCServer mcServer, MCServerConfigFile configFile, Handler<AsyncResult<JsonObject>> resultHandler) {
        FileSystem fileSystem = vertx.fileSystem();
        if (configFile.getFileName() != null && mcServer != null) {
            mcServerDao.getServerLocationById(mcServer).onSuccess(rows -> {
                MCServer server = DBPool.objectMapping(MCServer.class, rows);
                if (server != null && server.getLocation() != null) {
                    JsonObject translate = new JsonObject();
                    JsonObject jsonObject = new JsonObject();
                    Future<Buffer> fileFuture = fileSystem.readFile(SysConfig.getCorePath(server.getLocation()) + configFile.getFileName());
                    Future<Buffer> translateFuture = fileSystem.readFile(SysConfig.getTranslateFile(configFile.getFileName()));
                    CompositeFuture.all(fileFuture, translateFuture).onSuccess(handler -> {
                                Buffer fileBuffer = handler.resultAt(0);
                                Buffer translateBuffer = handler.resultAt(1);
                                if (configFile.getFileName().contains("properties") || configFile.getFileName().contains("eula.txt")) {
                                    try {
                                        Properties properties = new Properties();
                                        properties.load(new StringReader(fileBuffer.toString()));
                                        Map<String, String> map = Maps.fromProperties(properties);
                                        jsonObject.mergeIn(JsonObject.mapFrom(map));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    Yaml yaml = new Yaml();
                                    jsonObject.mergeIn(JsonObject.mapFrom(yaml.load(fileBuffer.toString())));
                                }
                                translate.mergeIn(translateBuffer.toJsonObject());
                            }).onFailure(Throwable::printStackTrace)
                            .onComplete(compositeFutureAsyncResult -> resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", new JsonObject().put("config", jsonObject).put("translate", translate)))));
                }

            });
        }
    }

    @Override
    public void getPluginList(Vertx vertx, MCServer mcServer, Handler<AsyncResult<JsonObject>> resultHandler) {
        mcServerDao.getServerLocationById(mcServer).onSuccess(rows -> {
            MCServer server = DBPool.objectMapping(MCServer.class, rows);
            if (server != null && server.getLocation() != null) {
                vertx.fileSystem().readDir(SysConfig.getCorePath(server.getLocation()) + "/plugins", "\\w*.*.(jar|disabled|disabled)").onSuccess(list -> {
                    JsonObject object = new JsonObject();
                    list.replaceAll(f -> f.substring(f.lastIndexOf("\\") + 1));
                    object.put("data", list);
                    resultHandler.handle(Future.succeededFuture(object));
                });
            }
        }).onFailure(throwable -> {
            throwable.printStackTrace();
            resultHandler.handle(Future.failedFuture(throwable));
        });
    }

    @Override
    public void disablePlugins(Vertx vertx, MCServer mcServer, String plugin, Handler<AsyncResult<JsonObject>> resultHandler) {
        FileSystem fs = vertx.fileSystem();
        mcServerDao.getServerLocationById(mcServer).onSuccess(rows -> {
            MCServer server = DBPool.objectMapping(MCServer.class, rows);
            if (server != null && server.getLocation() != null) {
                String pluginPath = SysConfig.getCorePath(server.getLocation()) + "/plugins/" + plugin;
                fs.exists(pluginPath).onSuccess(exist -> {
                    if (exist) {
                        fs.move(pluginPath, pluginPath + ".disabled").onSuccess(handler -> resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg", plugin + "已禁用")))).onFailure(e -> {
                            e.printStackTrace();
                            resultHandler.handle(Future.failedFuture("禁用失败"));
                        });
                    } else {
                        resultHandler.handle(Future.failedFuture("禁用失败"));
                    }
                });
            }
        }).onFailure(throwable -> {
            throwable.printStackTrace();
            resultHandler.handle(Future.failedFuture(throwable));
        });
    }

    @Override
    public void enablePlugins(Vertx vertx, MCServer mcServer, String plugin, Handler<AsyncResult<JsonObject>> resultHandler) {
        FileSystem fs = vertx.fileSystem();
        mcServerDao.getServerLocationById(mcServer).onSuccess(rows -> {
            MCServer server = DBPool.objectMapping(MCServer.class, rows);
            if (server != null && server.getLocation() != null) {
                //被禁用的插件名
                String pluginPath = SysConfig.getCorePath(server.getLocation()) + "/plugins/" + plugin;
                //开启后的地址
                String enablePluginPath = pluginPath.replace(".disabled", "");
                fs.exists(pluginPath).onSuccess(exist -> {
                    if (exist) {
                        fs.move(pluginPath, enablePluginPath).onSuccess(handler -> resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg", plugin.replace(".disabled", "") + "已启用")))).onFailure(e -> {
                            e.printStackTrace();
                            resultHandler.handle(Future.failedFuture("启用失败"));
                        });
                    } else {
                        fs.exists(enablePluginPath).onSuccess(pluginExist -> {
                            if (pluginExist) {
                                resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg", pluginPath.replace(".disabled", "") + "已启用")));
                            } else {
                                resultHandler.handle(Future.failedFuture("启用失败,可能是插件不存在"));
                            }
                        });
                    }
                });
            }
        }).onFailure(throwable -> {
            throwable.printStackTrace();
            resultHandler.handle(Future.failedFuture("启用失败"));
        });
    }

    @Override
    public void deletePlugins(Vertx vertx, MCServer mcServer, String plugin, Handler<AsyncResult<JsonObject>> resultHandler) {
        FileSystem fs = vertx.fileSystem();
        mcServerDao.getServerLocationById(mcServer).onSuccess(rows -> {
            MCServer server = DBPool.objectMapping(MCServer.class, rows);
            if (server != null && server.getLocation() != null) {
                //被禁用的插件名
                String pluginPath = SysConfig.getCorePath(server.getLocation()) + "/plugins/" + plugin;
                //开启后的地址
                String enablePluginPath = pluginPath.replace(".disabled", "");
                fs.exists(pluginPath).onSuccess(exist -> {
                    if (exist) {
                        fs.delete(pluginPath).onSuccess(handler -> resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg", plugin.replace(".disabled", "") + "已删除")))).onFailure(e -> {
                            e.printStackTrace();
                            resultHandler.handle(Future.failedFuture("删除失败,可能是插件不存在"));
                        });
                    } else {
                        fs.exists(enablePluginPath).onSuccess(pluginExist -> {
                            if (pluginExist) {
                                fs.delete(enablePluginPath).onSuccess(handler -> resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg", plugin.replace(".disabled", "") + "已删除"))));
                            } else {
                                resultHandler.handle(Future.failedFuture("删除失败,可能是插件不存在"));
                            }
                        });
                    }
                });
            }
        }).onFailure(throwable -> {
            throwable.printStackTrace();
            resultHandler.handle(Future.failedFuture("删除失败"));
        });
    }

    @Override
    public void uploadPlugins(Vertx vertx, MCServer server, FileUpload file, Handler<AsyncResult<JsonObject>> resultHandler) {
        FileSystem fs = vertx.fileSystem();
        mcServerDao.getServerById(server).compose(rows -> Future.succeededFuture(DBPool.camelMapping(rows).mapTo(MCServer.class))).compose(mcServer -> {
                    server.setLocation(SysConfig.getCorePath(mcServer.getLocation()));
                    return fs.exists(server.getLocation() + "plugins");
                }).compose(exist -> {
                    if (exist) {
                        return fs.copy(file.uploadedFileName(), server.getLocation() + "plugins/" + file.fileName());
                    } else {
                        return Future.failedFuture("上传失败");
                    }
                }).onSuccess(v -> resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg", "上传成功"))))
                .onFailure(throwable -> {
                    throwable.printStackTrace();
                    resultHandler.handle(Future.failedFuture((throwable)));
                });
    }

    @Override
    public void serverStart(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler) {
        boolean serverStatus = EnvOptions.getServerStatus();
        // 如果已创建Process(启动服务器)则直接跳过
        if (serverStatus) {
            resultHandler.handle(Future.succeededFuture());
        } else {
            getEnableServer(ar -> {
                MCServer mcServer;
                mcServer = Json.decodeValue(ar.result().toString(), MCServer.class);

                if (mcServer.getId() != null) {
                    mcSettingDao.getSettingById(mcServer.getId()).onSuccess(rows -> {
                        File location = new File(DIR + mcServer.getLocation());
                        System.out.println(location.exists());
                        if (location.exists() && rows.iterator().hasNext()) {
                            try {
                            MCSetting setting = Json.decodeValue(DBPool.camelMapping(rows).toString(), MCSetting.class);
                            Server = new ProcessServer(new File(DIR + mcServer.getLocation()), setting.returnCMD(), vertx);
                                /*
                                 * 用于测试CMD使用
                                 * */
//                                Server = new ProcessServer(new File(DIR + mcServer.getLocation()), CMD, vertx);
                                Server.start();
                                resultHandler.handle(Future.succeededFuture(new JsonObject().put("code", 200).put("msg", "启动成功")));
                                EnvOptions.setServerStatus(true);
                            } catch (IOException e) {
                                resultHandler.handle(Future.succeededFuture(new JsonObject().put("code", 500).put("msg", "启动失败成功").put("data", e.getStackTrace())));
                                e.printStackTrace();
                            }
                        } else {
                            resultHandler.handle(Future.succeededFuture(new JsonObject().put("code", 400).put("msg", "启动失败，请扫描服务器")));
                        }
                    }).onFailure(e -> {
                        e.printStackTrace();
                        resultHandler.handle(Future.succeededFuture(new JsonObject().put("code", 500).put("msg", "启动失败成功").put("data", e.getStackTrace())));
                    });


                }
            });

        }
    }

    @Override
    public boolean serverStop(Vertx vertx) {
        if (Server != null) {
            Server.stop();
            Server = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean serverStatus() {
        return EnvOptions.getServerStatus();
    }

    @Override
    public void getEnableServer(Handler<AsyncResult<JsonObject>> resultHandler) {
        mcServerDao.getServerByEnable(true).onSuccess(ar -> {
            JsonObject object = DBPool.camelMapping(ar);
            resultHandler.handle(Future.succeededFuture(object));
        }).onFailure(Throwable::printStackTrace);
    }
}
