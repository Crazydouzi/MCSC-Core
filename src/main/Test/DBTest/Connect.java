package DBTest;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import makjust.entity.User;
import makjust.utils.SysConfig;

import java.util.ArrayList;
import java.util.List;

public class Connect {
    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        final JsonObject config = new JsonObject()
                .put("url", "jdbc:sqlite:" + SysConfig.resourcesPath() + "core.db")
                .put("driver_class", "org.sqlite.JDBC")
                .put("max_pool_size", 30);
        JDBCPool pool = JDBCPool.pool(vertx, config);
        pool.query("select * from user;")
                .execute()
                .onFailure(Throwable::printStackTrace)
                .onSuccess(rows -> {
                    List<User> userList = new ArrayList<>();
                    for (Row row : rows) {
                        User user = new User();
                        user = Json.decodeValue(row.toJson().toString(), user.getClass());
                        userList.add(user);

                    }
                    System.out.println(userList);
                }).onComplete(r->{
                    pool.close();
        });
    }


}
