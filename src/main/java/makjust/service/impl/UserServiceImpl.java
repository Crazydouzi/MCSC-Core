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
import makjust.entity.User;
import makjust.service.UserService;

public class UserServiceImpl implements UserService {
    private UserDao userDao=new UserDaoImpl();
    @Override
    public void userLogin(Vertx vertx,User user, Handler<AsyncResult<JsonObject>> resultHandler) {
        userDao.selectUserByName(vertx,user.getUsername()).onSuccess(ar->{
            JsonObject result = new JsonObject();
            for (Row row:ar){
                result = row.toJson();
            }
            if (result.isEmpty()){
                resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg","用户不存在")));
            }
            else if (result.getString("pwd").equals(user.getPwd())){
                resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg","登录成功")));
            }else{
                resultHandler.handle(Future.succeededFuture(new JsonObject().put("msg","密码错误")));

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
