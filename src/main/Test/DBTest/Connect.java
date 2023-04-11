package DBTest;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import makjust.pojo.MCServer;
import makjust.pojo.User;
import makjust.utils.DBPool;
import makjust.utils.SysConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class Connect {
    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        final JsonObject config = new JsonObject().put("url", "jdbc:sqlite:" + SysConfig.resourcesPath() + "config/core.db").put("driver_class", "org.sqlite.JDBC").put("max_pool_size", 30);
        JDBCPool pool = JDBCPool.pool(vertx, config);
        pool.query("select * from user;").execute().onFailure(Throwable::printStackTrace).onSuccess(rows -> {
            List<User> userList = new ArrayList<>();
            for (Row row : rows) {
                User user = new User();
                user = Json.decodeValue(row.toJson().toString(), user.getClass());
                userList.add(user);
            }
            System.out.println(userList);
        }).onComplete(r -> {
            pool.close();
        });
    }

    @Test
    void queryTest() {
        Vertx vertx = Vertx.vertx();
        DBPool.conn(vertx);
        DBPool.executeRowSQL("select * from mc_server;").onSuccess(ar -> {
            JsonObject mapping = new JsonObject();
            mapping.put("id", "id");
            mapping.put("server_name", "serverName");
            mapping.put("version", "version");
            mapping.put("location", "location");
            mapping.put("enable", "enable");

            JsonObject v = DBPool.mapping(mapping, ar);
            System.out.println(v);
            List<MCServer> mcServerList=new ArrayList<>();
            v.getJsonArray("data").forEach(al->mcServerList.add(Json.decodeValue(al.toString(),MCServer.class)));
            System.out.println(mcServerList);
            System.out.println("完成");
//                    MCServer mcServer = new MCServer();
//                    for (Row row : ar) {
//                        mcServer = Json.decodeValue(row.toJson().toString(), MCServer.class);
//                    }
//                    System.out.println(mcServer);
        }).onFailure(Throwable::printStackTrace);

    }

    @Test
    void insertTest() {
        DBPool.conn(Vertx.vertx());
        JsonObject object = new JsonObject();
        object.put("id",1);
        object.put("username", "test");
        object.put("pwd", "test");
        object.put("role", "r");
        DBPool.insert("user", object).onSuccess(ar -> {
            for (Row row:ar){
                System.out.println(row.toJson());
            }
            System.out.println(ar.rowCount());
        }).onComplete(ar -> {
            System.out.println(ar.result());
        }).onFailure(Throwable::printStackTrace);
    }

    @AfterAll
    static void sleep() throws InterruptedException {
        Thread.sleep(700);
    }


}
