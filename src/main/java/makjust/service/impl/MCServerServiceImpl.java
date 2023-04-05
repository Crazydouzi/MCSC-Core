package makjust.service.impl;

import com.google.common.collect.Maps;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.dao.MCConfigFileDao;
import makjust.dao.MCServerDao;
import makjust.dao.MCSettingDao;
import makjust.dao.impl.MCConfigFileDaoImpl;
import makjust.dao.impl.MCServerDaoImpl;
import makjust.dao.impl.MCSettingDaoImpl;
import makjust.entity.MCServer;
import makjust.entity.MCServerConfigFile;
import makjust.entity.MCSetting;
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
                .onSuccess(ar -> resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "更新完成").put("code", 200))))
                .onFailure(e -> {
                    e.printStackTrace();
                    resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "更新失败").put("code", 500)));
                });
    }

    @Override
    public void setCoreSetting(MCSetting setting, Handler<AsyncResult<JsonObject>> resultHandler) {
        mcSettingDao.updateSetting(setting)
                .onSuccess(ar -> resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "更新完成"))))
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
                    Future<Buffer> fileFuture=fileSystem.readFile(SysConfig.getCorePath(server.getLocation()) + configFile.getFileName());
                    Future<Buffer> translateFuture=fileSystem.readFile(SysConfig.getTranslateFile(configFile.getFileName()));
                    CompositeFuture.all(fileFuture,translateFuture).onSuccess(handler->{
                        Buffer fileBuffer=handler.resultAt(0);
                        Buffer translateBuffer=handler.resultAt(1);
                        if (configFile.getFileName().contains("properties")||configFile.getFileName().contains("eula.txt")) {
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
                            MCSetting setting = Json.decodeValue(DBPool.camelMapping(rows).toString(), MCSetting.class);
                            try {
//                            Server = new ProcessServer(new File(DIR + mcServer.getLocation()), setting.getCMD(), vertx);
                                /*
                                 * 用于测试CMD使用
                                 * */
                                Server = new ProcessServer(new File(DIR + mcServer.getLocation()), CMD, vertx);
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
            System.gc();
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
