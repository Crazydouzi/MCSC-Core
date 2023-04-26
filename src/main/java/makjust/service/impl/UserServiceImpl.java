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
import makjust.pojo.User;
import makjust.service.UserService;

public class UserServiceImpl implements UserService {
    private final UserDao userDao=new UserDaoImpl();
    @Override
    public void userLogin(Vertx vertx,User user, Handler<AsyncResult<JsonObject>> resultHandler) {
        userDao.selectUserByName(vertx,user).onSuccess(ar->{
            JsonObject result = new JsonObject();
            for (Row row:ar){
                result = row.toJson();
            }
            if (ar.size()<=0){
                resultHandler.handle(Future.failedFuture("用户或密码错误"));
            }
          else{
                resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg", "登录成功").put("data",result)));
            }
        });
    }

    @Override
    public void addUser(Vertx vertx,User user, Handler<AsyncResult<JsonObject>> resultHandler) {
        userDao.insertUser(vertx,user).onSuccess(ar->{
            JsonArray jsonArray=new JsonArray();
            for (Row row:ar){
                jsonArray.add(row.toJson());
            }
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("data",jsonArray)
            ));

        });
    }

    @Override
    public void modifyUser(Vertx vertx,User user, Handler<AsyncResult<JsonObject>> resultHandler) {
        userDao.updateUser(vertx,user).onSuccess(ar-> resultHandler.handle(Future.succeededFuture(new JsonObject().put("data","更新完成")
        ))).onFailure(e->{
            e.printStackTrace();
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("data","更新失败")));
        });
    }
}
