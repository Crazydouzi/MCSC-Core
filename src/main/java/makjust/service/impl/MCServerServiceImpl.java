package makjust.service.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import makjust.dao.MCServerDao;
import makjust.dao.impl.MCServerDaoImpl;
import makjust.entity.MCServer;
import makjust.utils.EnvOptions;
import makjust.serverCore.ProcessServer;
import makjust.service.MCServerService;
import makjust.utils.SysConfig;

import java.io.File;
import java.io.IOException;

public class MCServerServiceImpl implements MCServerService {
    private String DIR = SysConfig.getCorePath("/");
    private String CMD = SysConfig.object.getJsonObject("mcServer").getString("def_cmd");
    private ProcessServer mcServer;
    private MCServerDao mcServerDao=new MCServerDaoImpl();
    @Override
    public JsonObject editSetting() {
        return new JsonObject();
    }


    @Override
    public void getSetting(Vertx vertx, MCServer mcServer, Handler<AsyncResult<JsonObject>> resultHandler) {
        mcServerDao.getSettingById(mcServer.getId()).onSuccess(ar->{
            JsonArray jsonArray=new JsonArray();
            jsonArray.add(mcServer);
            for (Row row:ar){
                jsonArray.add(row.toJson());
            }
            System.out.println(jsonArray);
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("data",jsonArray)));
        });
    }

    @Override
    public boolean serverStart(Vertx vertx) {
        boolean serverStatus = EnvOptions.getServerStatus();
        // 如果已创建Process(启动服务器)则直接跳过
        if (serverStatus) return false;
        mcServer = new ProcessServer(new File(DIR), CMD, vertx);
        try {
            mcServer.start();
            EnvOptions.setServerStatus(true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean serverStop(Vertx vertx) {
        boolean serverStatus = EnvOptions.getServerStatus();
        if (serverStatus&&mcServer!=null) {
            try {
                mcServer.stop();
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
    public JsonObject getEnableServer() {
        return null;
    }
}
