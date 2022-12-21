package makjust.dao.impl;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.dao.UserDao;
import makjust.entity.User;
import makjust.utils.DBUtil;

import java.util.ArrayList;

public class UserDaoImpl implements UserDao {
    private DBUtil dbUtil=new DBUtil();
    //    private Vertx vertx;
//    public UserDaoImpl(Vertx vertx){
//        this.vertx=vertx;
//    }
    @Override
    public Future<RowSet<Row>> insertUser(Vertx vertx,User user) {
        return dbUtil.executeRowSQL("insert into user ();");
    }

    @Override
    public Future<RowSet<Row>> updateUser(Vertx vertx,User user) {
        JsonObject object=JsonObject.mapFrom(user);
        return dbUtil.update("user",object);
    }

    @Override
    public Future<RowSet<Row>> selectUser(Vertx vertx) {
        return dbUtil.executeRowSQL("select * from User;");
//        vertx.eventBus().consumer("userDao.selectUser").handler(message->{
//            dbUtil.query("select * from User;",new JsonArray()).onSuccess(ar->{
//               message.reply(dbUtil.toJson(ar));
//            });
//        });
    }

    @Override
    public Future<RowSet<Row>> selectUserByName(Vertx vertx, String username) {
        return dbUtil.executeRowSQL("select username,pwd from User where username=?;",username);
    }
}
