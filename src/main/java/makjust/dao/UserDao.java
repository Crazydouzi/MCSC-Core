package makjust.dao;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.pojo.User;

public interface UserDao {

    Future<RowSet<Row>> insertUser(User user);

    Future<RowSet<Row>> updateUser( User user);
    Future<RowSet<Row>> updateUserPwd( User user);
    Future<RowSet<Row>> selectUser();

    Future<RowSet<Row>> selectUserByNameAndPwd(User user);
    Future<RowSet<Row>> selectUserByName(User user);

}
