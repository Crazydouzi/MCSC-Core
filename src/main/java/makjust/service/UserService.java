package makjust.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import makjust.pojo.User;

public interface UserService {
    void userLogin(User user, Handler<AsyncResult<User>> resultHandler);

    void addUser(User user, Handler<AsyncResult<JsonObject>> resultHandler);

    void modifyUser(User user, Handler<AsyncResult<Boolean>> resultHandler);
    void modifyUserPwd(User user, Handler<AsyncResult<Boolean>> resultHandler);

    void  getUser(User user, Handler<AsyncResult<User>> resultHandler);

}
