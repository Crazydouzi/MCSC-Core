package makjust.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import makjust.entity.MCServer;

import java.util.Map;

public interface MCServerService {
    //核心基本配置设定
    void setCoreSetting(MCServer mcServer,Handler<AsyncResult<JsonObject>> resultHandler);
    //服务器配置设定
    void setServerSetting(Map<String,Object> optionMap,Handler<AsyncResult<JsonObject>> resultHandler);
    //获取设置
    void getSetting(Vertx vertx, MCServer mcServer, Handler<AsyncResult<JsonObject>> resultHandler);
    //开启服务器
    void serverStart(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler) ;
    //关闭服务器
    boolean serverStop(Vertx vertx);
    //获取可用服务器
    void getEnableServer(Handler<AsyncResult<JsonObject>> resultHandler);
}
