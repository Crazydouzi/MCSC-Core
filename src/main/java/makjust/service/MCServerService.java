package makjust.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import makjust.entity.MCServer;

import java.util.Map;

public interface MCServerService {
    void setCoreSetting(MCServer mcServer,Handler<AsyncResult<JsonObject>> resultHandler);
    void setServerSetting(Map<String,Object> optionMap,Handler<AsyncResult<JsonObject>> resultHandler);
    void getSetting(Vertx vertx, MCServer mcServer, Handler<AsyncResult<JsonObject>> resultHandler);
    void serverStart(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler) ;
    boolean serverStop(Vertx vertx);
    void getEnableServer(Handler<AsyncResult<JsonObject>> resultHandler);
}
