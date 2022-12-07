package makjust.service.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import makjust.dao.UserDao;
import makjust.dao.impl.UserDaoImpl;
import makjust.service.UserService;

public class UserServiceImpl implements UserService {
    private UserDao userDao=new UserDaoImpl();
    @Override
    public void findUser(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler) {
        userDao.selectUser(vertx).onSuccess(ar->{
            JsonArray jsonArray=new JsonArray();
            for (Row row:ar){
                jsonArray.add(row.toJson());
            }
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("data",jsonArray)
            ));

        });
    }

    @Override
    public void addUser(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler) {
        userDao.insertUser(vertx).onSuccess(ar->{
            JsonArray jsonArray=new JsonArray();
            for (Row row:ar){
                jsonArray.add(row.toJson());
            }
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("data",jsonArray)
            ));

        });
    }

    @Override
    public void modifyUser(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler) {
        userDao.updateUser((vertx)).onSuccess(ar->{
            JsonArray jsonArray=new JsonArray();
            for (Row row:ar){
                jsonArray.add(row.toJson());
            }
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("data",jsonArray)
            ));

        });
    }
}
