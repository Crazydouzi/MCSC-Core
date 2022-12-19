package makjust.dao;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.dao.impl.UserDaoImpl;
import makjust.entity.User;

@ProxyGen
public interface UserDao {
//    static UserDao create(Vertx vertx) {
//        return new UserDaoImpl(vertx);
//    }
//
//    static UserDao createProxy(Vertx vertx, String address) {
//        return new ServiceProxyBuilder(vertx)
//                .setAddress(address)
//                .build(UserDao.class);
//    }

    Future<RowSet<Row>> insertUser(Vertx vertx,User user);

    Future<RowSet<Row>> updateUser(Vertx vertx);

    Future<RowSet<Row>> selectUser(Vertx vertx);

    Future<RowSet<Row>> selectUserByName(Vertx vertx,String username);
}
