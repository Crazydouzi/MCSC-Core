package makjust.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import makjust.entity.User;

@ProxyGen
public interface UserService {
    void userLogin(Vertx vertx, User user,Handler<AsyncResult<JsonObject>> resultHandler);
    void addUser(Vertx vertx, User user, Handler<AsyncResult<JsonObject>> resultHandler);
    void modifyUser(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler);

}
