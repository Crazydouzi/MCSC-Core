package makjust.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import makjust.pojo.User;

public interface UserService {
    void userLogin(Vertx vertx, User user, Handler<AsyncResult<JsonObject>> resultHandler);

    void addUser(Vertx vertx, User user, Handler<AsyncResult<JsonObject>> resultHandler);

    void modifyUser(Vertx vertx, User user, Handler<AsyncResult<JsonObject>> resultHandler);

}
