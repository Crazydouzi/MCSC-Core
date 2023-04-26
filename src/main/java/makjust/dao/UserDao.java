package makjust.dao;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.pojo.User;

public interface UserDao {

    Future<RowSet<Row>> insertUser(Vertx vertx, User user);

    Future<RowSet<Row>> updateUser(Vertx vertx, User user);

    Future<RowSet<Row>> selectUser(Vertx vertx);

    Future<RowSet<Row>> selectUserByName(Vertx vertx, User user);
}
