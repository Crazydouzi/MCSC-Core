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
import makjust.utils.EnvOptions;
import makjust.utils.SysConfig;

import java.io.File;
import java.io.IOException;

public class MCServerServiceImpl implements MCServerService {
    private final String DIR = SysConfig.getCorePath("/");
    // 默认CMD为Windows 若需要为Linux还需修改
    private final String CMD = "CMD";
    private ProcessServer Server;
    private final MCServerDao mcServerDao = new MCServerDaoImpl();
    private final MCSettingDao mcSettingDao = new MCSettingDaoImpl();


    @Override
    public void setCoreSetting(MCServer mcServer, Handler<AsyncResult<JsonObject>> resultHandler) {

    }

    @Override
    public void setServerSetting(MCSetting setting, Handler<AsyncResult<JsonObject>> resultHandler) {
//        resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", list)));
    }

    @Override
    public void getSetting(Vertx vertx, MCServer mcServer, Handler<AsyncResult<JsonObject>> resultHandler) {
        mcSettingDao.getSettingById(mcServer.getId()).onSuccess(ar -> {
            JsonObject object = new JsonObject();
            for (Row row : ar) {
                object = row.toJson();
            }
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", object)));
        });
    }

    @Override
    public void serverStart(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler) {
        boolean serverStatus = EnvOptions.getServerStatus();
        // 如果已创建Process(启动服务器)则直接跳过
        if (serverStatus) {
            resultHandler.handle(Future.succeededFuture());
            return;
        }
        getEnableServer(ar -> {
            MCServer mcServer;
            mcServer = Json.decodeValue(ar.result().toString(), MCServer.class);
            if (mcServer != null) {
                File location = new File(DIR + mcServer.getLocation());
                if (!location.exists()) {
                    resultHandler.handle(Future.failedFuture("启动失败，服务器不存在！请扫描服务器"));
                } else {
                    Server = new ProcessServer(new File(DIR + mcServer.getLocation()), CMD, vertx);
                    try {
                        Server.start();
                        resultHandler.handle(Future.succeededFuture());
                        EnvOptions.setServerStatus(true);
                    } catch (IOException e) {
                        resultHandler.handle(Future.failedFuture("启动失败"));
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean serverStop(Vertx vertx) {
        boolean serverStatus = EnvOptions.getServerStatus();
        if (serverStatus && Server != null) {
            try {
                Server.stop();
                EnvOptions.setServerStatus(false);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
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
            resultHandler.handle(Future.succeededFuture(object));
        }).onFailure(Throwable::printStackTrace);
    }
}
