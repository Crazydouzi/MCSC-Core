package makjust.service.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.HashString;
import io.vertx.ext.auth.impl.hash.SHA256;
import io.vertx.sqlclient.Row;
import makjust.dao.UserDao;
import makjust.dao.impl.UserDaoImpl;
import makjust.bean.User;
import makjust.service.UserService;
import makjust.utils.DBPool;
import makjust.utils.SysConfig;

public class UserServiceImpl implements UserService {
    private final UserDao userDao = new UserDaoImpl();
    private final SHA256 sha256 = new SHA256();

    @Override
    public void userLogin(User user, Handler<AsyncResult<User>> resultHandler) {
        //加密
        user.setPwd(sha256.hash(new HashString((String) SysConfig.getConf("salt")), user.getPwd()));
        userDao.selectUserByNameAndPwd(user).onSuccess(ar -> {
            JsonObject result = DBPool.camelMapping(ar);
            if (ar.size() <= 0) {
                resultHandler.handle(Future.failedFuture("用户或密码错误"));
            } else {

                resultHandler.handle(Future.succeededFuture(result.mapTo(User.class)));
            }
        });
    }

    @Override
    public void addUser(User user, Handler<AsyncResult<JsonObject>> resultHandler) {
        userDao.insertUser(user).onSuccess(ar -> {
            JsonArray jsonArray = new JsonArray();
            for (Row row : ar) {
                jsonArray.add(row.toJson());
            }
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", jsonArray)
            ));

        });
    }

    @Override
    public void modifyUser(User user, Handler<AsyncResult<Boolean>> resultHandler) {
        //加密
        if (!user.getPwd().isEmpty()) {
            user.setPwd(sha256.hash(new HashString((String) SysConfig.getConf("salt")), user.getPwd()));
        }
        userDao.updateUser(user).onSuccess(ar -> resultHandler.handle(Future.succeededFuture(true
        ))).onFailure(e -> {
            e.printStackTrace();
            resultHandler.handle(Future.succeededFuture(false));
        });
    }
    public void modifyUserPwd(User user, Handler<AsyncResult<Boolean>> resultHandler) {
        //加密
        if (!user.getPwd().isEmpty()) {
            user.setPwd(sha256.hash(new HashString((String) SysConfig.getConf("salt")), user.getPwd()));
        }
        userDao.updateUserPwd(user).onSuccess(ar -> resultHandler.handle(Future.succeededFuture(true
        ))).onFailure(e -> {
            e.printStackTrace();
            resultHandler.handle(Future.succeededFuture(false));
        });
    }

    @Override
    public void getUser(User user, Handler<AsyncResult<User>> resultHandler) {
        userDao.selectUserByName(user).onSuccess(rows -> {
                    if (rows.size() > 0) {
                        resultHandler.handle(Future.succeededFuture(user));
                    } else {
                        resultHandler.handle(Future.succeededFuture(null));
                    }
                })
                .onFailure(throwable -> {
                    throwable.printStackTrace();
                    resultHandler.handle(Future.failedFuture(throwable.getCause().getMessage()));
                });
    }
}
