package makjust.dao.impl;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.dao.UserDao;
import makjust.pojo.User;
import makjust.utils.DBPool;

public class UserDaoImpl implements UserDao {

    @Override
    public Future<RowSet<Row>> insertUser(Vertx vertx,User user) {
        return DBPool.insert("User", user);
    }

    @Override
    public Future<RowSet<Row>> updateUser(Vertx vertx,User user) {
        return DBPool.update("update user set pwd=#{pwd},username=#{username} where id=#{id};",user);
    }

    @Override
    public Future<RowSet<Row>> selectUser(Vertx vertx) {
        return DBPool.executeRowSQL("select * from User;");
    }

    @Override
    public Future<RowSet<Row>> selectUserByName(Vertx vertx,User user) {
        return DBPool.executeSQL("select id,username from user where username=#{username} and pwd=#{pwd};",user);
    }
}
