package makjust.dao.impl;

import ch.qos.logback.core.db.dialect.DBUtil;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.dao.UserDao;
import makjust.entity.User;
import makjust.utils.DBUtils;

public class UserDaoImpl implements UserDao {
    //    private Vertx vertx;
//    public UserDaoImpl(Vertx vertx){
//        this.vertx=vertx;
//    }
    @Override
    public Future<RowSet<Row>> insertUser(Vertx vertx,User user) {
        return DBUtils.insert("User", user);
    }

    @Override
    public Future<RowSet<Row>> updateUser(Vertx vertx,User user) {
        JsonObject object=JsonObject.mapFrom(user);
        return DBUtils.update("user",object);
    }

    @Override
    public Future<RowSet<Row>> selectUser(Vertx vertx) {
        return DBUtils.executeRowSQL("select * from User;");
    }

    @Override
    public Future<RowSet<Row>> selectUserByName(Vertx vertx, String username) {
        return DBUtils.executeRowSQL("select username,pwd from User where username=?;",username);
    }
}
