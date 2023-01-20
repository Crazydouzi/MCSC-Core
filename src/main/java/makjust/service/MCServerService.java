package makjust.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import makjust.entity.MCServer;
public interface MCServerService {
    JsonObject editSetting();
    void getSetting(Vertx vertx, MCServer mcServer, Handler<AsyncResult<JsonObject>> resultHandler);
    void serverStart(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler) ;
    boolean serverStop(Vertx vertx);
    JsonObject getEnableServer();
}
