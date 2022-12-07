package makjust.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@ProxyGen
public interface UserService {
    void findUser(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler);
    void addUser(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler);
    void modifyUser(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler);

}
