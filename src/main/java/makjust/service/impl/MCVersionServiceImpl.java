package makjust.service.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import makjust.dao.MCServerDao;
import makjust.dao.MCSettingDao;
import makjust.dao.impl.MCServerDaoImpl;
import makjust.dao.impl.MCSettingDaoImpl;
import makjust.dto.RemoteVersionInfoDTO;
import makjust.pojo.MCServer;
import makjust.pojo.MCSetting;
import makjust.service.MCVersionService;
import makjust.utils.DBPool;
import makjust.utils.EnvOptions;
import makjust.utils.SysConfig;

import java.util.List;
import java.util.UUID;

import static makjust.utils.DBPool.getLastRowId;

public class MCVersionServiceImpl implements MCVersionService {
    private final String DIR = SysConfig.getCorePath("/");
    private final MCServerDao mcServerDao = new MCServerDaoImpl();
    private final MCSettingDao mcSettingDao = new MCSettingDaoImpl();

    @Override
    public void getVersionList(Handler<AsyncResult<JsonObject>> resultHandler) {
        mcServerDao.selectMCServerList().onComplete(ar -> {
            if (ar != null) {
                resultHandler.handle(Future.succeededFuture(DBPool.camelMapping(ar.result())));
            }
        });

    }

    @Override
    public JsonObject getStoreVersionList() {
        return new JsonObject();
    }

    @Override
    public void serverScanner(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler) {
        FileSystem fileSystem = vertx.fileSystem();
        JsonArray jsonArray = new JsonArray();
        fileSystem.readDir(DIR, "[^.]*").onSuccess(dirList -> {
            for (String loc : dirList) {
                List<String> jarList = fileSystem.readDirBlocking(loc, "\\w*.jar");
                // 空文件夹不返回
                if (!jarList.isEmpty()) {
                    jarList.replaceAll(f -> f.substring(f.lastIndexOf("\\") + 1));
                    jsonArray.add(new JsonObject().put(loc.substring(loc.lastIndexOf("\\") + 1), jarList));
                }
            }
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", jsonArray)));

        });
    }

    @Override
    public void changeEnableVersion(MCServer server, Handler<AsyncResult<JsonObject>> resultHandler) {
        server.setEnable(true);
        boolean serverStatus = EnvOptions.getServerStatus();
        if (serverStatus) {
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "服务器运行中！无法切换版本")));
        } else {
            mcServerDao.updateMCServerEnable(server).onSuccess(ar -> {
                if (ar.rowCount() >= 1) {
                    resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "切换成功")));
                } else {
                    resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "切换失败")));
                }
            }).onFailure(Throwable::printStackTrace);
        }

    }

    @Override
    public void installMCServerFromRemote(Vertx vertx, MCServer server, MCSetting setting, RemoteVersionInfoDTO versionInfo, Handler<AsyncResult<JsonObject>> resultHandler) {
        FileSystem fs = vertx.fileSystem();
        String fileLOC = SysConfig.getCorePath(server.getServerName() + "-" + server.getVersion() + "-" + UUID.randomUUID());
        String jarFileLoc = fileLOC + versionInfo.getCoreName();
        fs.mkdir(fileLOC)
                .compose(v -> fs.createFile(jarFileLoc))
                .compose(v -> {
                    WebClient webClient = WebClient.create(vertx);
                    String filePath = "https://api.papermc.io/v2/projects/" + versionInfo.getFrom() + "/versions/" + versionInfo.getVersion() + "/builds/" + versionInfo.getBuildCode() + "/downloads/" + versionInfo.getCoreName();
                    return webClient.getAbs(filePath)
                            .as(BodyCodec.buffer()).send();
                }).compose(v -> {
                    //如果不是jar类型直接结束
                    if (!v.headers().get("Content-Type").equals("application/java-archive")) {
                        return Future.failedFuture(v.bodyAsJsonObject().toString());
                    } else {
                        return fs.writeFile(jarFileLoc, v.bodyAsBuffer()).onSuccess(s -> {
                            server.setLocation(server.getServerName() + "-" + server.getVersion() + "-" + UUID.randomUUID());
                            setting.setJarName(versionInfo.getCoreName());
                        });
                    }
                }).compose(v -> mcServerDao.insertMCServer(server)
                        .compose(rows -> getLastRowId())
                        .compose(rows -> {
                            int id = rows.iterator().next().getInteger("id");
                            setting.setServerId(id);
                            return mcSettingDao.insertSetting(setting);
                        }).onFailure(throwable -> {
                            throwable.printStackTrace();
                            resultHandler.handle(Future.failedFuture("数据库插入失败，请尝试扫描服务器"));
                        }))
                .onSuccess(successResult -> resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "新建成功！"))))
                .onFailure(throwable -> {
                    throwable.printStackTrace();
                    resultHandler.handle(Future.failedFuture(throwable.getMessage()));
                });
    }

    @Override
    public void uploadMCServer(Vertx vertx, FileUpload file, MCServer server, MCSetting setting, Handler<AsyncResult<JsonObject>> resultHandler) {
        FileSystem fileSystem = vertx.fileSystem();
        String fileLOC = SysConfig.getCorePath(server.getServerName() + "-" + server.getVersion() + "-" + UUID.randomUUID());
        Future<Void> f = fileSystem.mkdir(fileLOC);
        System.out.println(file.fileName().endsWith(".jar"));
        if (file.fileName().endsWith(".jar")) {
            f.compose(v1 ->{
                server.setLocation(server.getServerName() + "-" + server.getVersion() + "-" + UUID.randomUUID());
                setting.setJarName(file.fileName());
                System.out.println(file.name());
                System.out.println(fileLOC  + file.fileName());
                return fileSystem.move(file.uploadedFileName(), fileLOC  + file.fileName());
                    }
            );
        } else if (file.fileName().endsWith(".zip")) {
            //Zip解压识别
        } else {
            f.compose(v -> Future.failedFuture("文件类型错误"));
        }
        f.compose(v -> mcServerDao.insertMCServer(server)
                        .compose(rows -> {
                            if (rows.rowCount()>=1){
                                return getLastRowId();
                            }else {
                                return Future.failedFuture("插入失败");
                            }
                        })
                        .compose(rows -> {
                            int id = rows.iterator().next().getInteger("id");
                            setting.setServerId(id);
                            return mcSettingDao.insertSetting(setting);
                        }).onFailure(throwable -> {
                            throwable.printStackTrace();
                            resultHandler.handle(Future.failedFuture("数据库插入失败，请尝试扫描服务器"));
                        }))
                .onSuccess(successResult -> resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "新建成功！"))))
                .onFailure(throwable -> {
                    throwable.printStackTrace();
                    resultHandler.handle(Future.failedFuture(throwable.getMessage()));
                });

    }
}
