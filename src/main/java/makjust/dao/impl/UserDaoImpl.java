package makjust.dao.impl;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.dao.UserDao;
import makjust.utils.DBUtil;

public class UserDaoImpl implements UserDao {
    private DBUtil dbUtil=new DBUtil();
    //    private Vertx vertx;
//    public UserDaoImpl(Vertx vertx){
//        this.vertx=vertx;
//    }
    @Override
    public Future<RowSet<Row>> insertUser(Vertx vertx) {
        return dbUtil.query("select role from User;",new JsonArray());
    }

    @Override
    public Future<RowSet<Row>> updateUser(Vertx vertx) {
        return dbUtil.query("select username from User;",new JsonArray());
    }

    @Override
    public Future<RowSet<Row>> selectUser(Vertx vertx) {
        return dbUtil.query("select * from User;",new JsonArray());
//        vertx.eventBus().consumer("userDao.selectUser").handler(message->{
//            dbUtil.query("select * from User;",new JsonArray()).onSuccess(ar->{
//               message.reply(dbUtil.toJson(ar));
//            });
//        });
    }
}
