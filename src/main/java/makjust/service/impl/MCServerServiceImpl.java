package makjust.service.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import makjust.dao.MCServerDao;
import makjust.dao.MCSettingDao;
import makjust.dao.impl.MCServerDaoImpl;
import makjust.dao.impl.MCSettingDaoImpl;
import makjust.entity.MCServer;
import makjust.entity.MCSetting;
import makjust.serverCore.ProcessServer;
import makjust.service.MCServerService;
import makjust.utils.DBPool;
import makjust.utils.EnvOptions;
import makjust.utils.SysConfig;

import java.io.File;
import java.io.IOException;

public class MCServerServiceImpl implements MCServerService {
    private final String DIR = SysConfig.getCorePath("/");
    // 默认CMD为Windows 若需要为Linux还需修改
    private final String CMD = "cmd";
    private ProcessServer Server;
    private final MCServerDao mcServerDao = new MCServerDaoImpl();
    private final MCSettingDao mcSettingDao = new MCSettingDaoImpl();


    @Override
    public void setCoreSetting(MCServer mcServer, Handler<AsyncResult<JsonObject>> resultHandler) {
        mcServerDao
                .updateMCServerInfo(mcServer)
                .onSuccess(ar -> resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "更新完成").put("code", 200))))
                .onFailure(e -> {
                    e.printStackTrace();
                    resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "更新失败").put("code", 500)));
                });
    }

    @Override
    public void setServerSetting(MCSetting setting, Handler<AsyncResult<JsonObject>> resultHandler) {
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
    public void serverStart(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler) {
        boolean serverStatus = EnvOptions.getServerStatus();
        // 如果已创建Process(启动服务器)则直接跳过
        if (serverStatus) {
            resultHandler.handle(Future.succeededFuture());
        } else {
            getEnableServer(ar -> {
                MCServer mcServer;
                mcServer = Json.decodeValue(ar.result().toString(), MCServer.class);

                if (mcServer != null) {
                    mcSettingDao.getSettingById(mcServer.getId()).onSuccess(rows -> {
                        MCSetting setting = Json.decodeValue(DBPool.camelMapping(rows).toString(),MCSetting.class);
                        File location = new File(DIR + mcServer.getLocation());
                        if (location.exists()&&setting != null) {
                            System.out.println(serverStatus);
                            System.out.println(setting);
                            Server = new ProcessServer(new File(DIR + mcServer.getLocation()), setting.getCMD(), vertx);
                            try {
                                Server.start();
                                resultHandler.handle(Future.succeededFuture());
                                EnvOptions.setServerStatus(true);
                            } catch (IOException e) {
                                resultHandler.handle(Future.failedFuture("启动失败"));
                                e.printStackTrace();
                            }
                        } else {
                            resultHandler.handle(Future.failedFuture("启动失败，服务器不存在！请扫描服务器"));
                        }
                    }).onFailure(e -> {
                        e.printStackTrace();
                        resultHandler.handle(Future.failedFuture("启动失败"));
                    });

                }
            });

        }
    }

    @Override
    public boolean serverStop(Vertx vertx) {
        if (Server!=null){
            Server.stop();
            Server=null;
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
            JsonObject object = new JsonObject();
            for (Row row : ar) {
                object = row.toJson();
            }
            if (object.isEmpty()) {
                object.put("data", null);
            }
            System.out.println(object);
            resultHandler.handle(Future.succeededFuture(JsonObject.mapFrom(Json.decodeValue(object.toString(), MCServer.class))));
        }).onFailure(Throwable::printStackTrace);
    }
}
