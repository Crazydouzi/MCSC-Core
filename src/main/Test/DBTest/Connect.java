package DBTest;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import makjust.entity.User;
import makjust.utils.DBUtil;
import makjust.utils.SysConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class Connect {
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

    @Test
    void queryTest() {
        Vertx vertx = Vertx.vertx();
        DBUtil dbUtil = new DBUtil();
        dbUtil.query("select * from user;", new JsonArray())
                .onSuccess(ar -> {
                    System.out.println("完成");
                    vertx.eventBus().publish("queryTest.query", DBUtil.toJsonArray(ar));
                })
                .onFailure(Throwable::printStackTrace)
                .onComplete(ar -> {
                    System.out.println(ar.result());
                    System.out.println(ar.mapEmpty());
                    System.out.println("完成");
                });
        vertx.eventBus().consumer("queryTest.query").handler(msg->{
            System.out.println(msg.body());
        });

    }

    @AfterAll
    static void sleep() throws InterruptedException {
        Thread.sleep(1500);
    }


}