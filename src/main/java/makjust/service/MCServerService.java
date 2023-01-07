package makjust.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import makjust.entity.MCServer;

import java.net.URISyntaxException;
@ProxyGen
public interface MCServerService {
    JsonObject editSetting();
    void getSetting(Vertx vertx, MCServer mcServer, Handler<AsyncResult<JsonObject>> resultHandler);
    boolean serverStart(Vertx vertx) ;
    boolean serverStop(Vertx vertx);
    JsonObject getEnableServer();
}
