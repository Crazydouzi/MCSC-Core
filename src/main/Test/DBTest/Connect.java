package DBTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import makjust.entity.MCServer;
import makjust.entity.User;
import makjust.utils.DBUtils;
import makjust.utils.SysConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class Connect {
    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        final JsonObject config = new JsonObject()
                .put("url", "jdbc:sqlite:" + SysConfig.resourcesPath() + "config/core.db")
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
//        JsonObject jsonObject=new JsonObject();
//        jsonObject.put("id",1);
//        jsonObject.put("server_name","test");
//        jsonObject.put("version","1");
//        jsonObject.put("location","1");
//        jsonObject.put("enable",1);
//        MCServer mcServer=Json.decodeValue(jsonObject.toString(), MCServer.class);
//        System.out.println(mcServer);
        Vertx vertx = Vertx.vertx();
        DBUtils.conn(vertx);
        DBUtils.executeRowSQL("select * from mc_server where enable=? LIMIT 1;",true)
                .onSuccess(ar -> {
                    System.out.println("完成");
                    MCServer mcServer=new MCServer();
                    for (Row row:ar){
                        mcServer=Json.decodeValue(row.toJson().toString(), MCServer.class);
//                        ObjectMapper mapper=new ObjectMapper();
//                        try {
//                            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
//                            mcServer=mapper.readValue(row.toJson().toString(),MCServer.class);
//
//                        } catch (JsonProcessingException e) {
//                            e.printStackTrace();
//                        }
                    }
                    System.out.println(mcServer);
                })
                .onFailure(Throwable::printStackTrace);

    }
    @Test
    void insertTest(){
        DBUtils dbUtil = new DBUtils();
        DBUtils.conn(Vertx.vertx());
        JsonObject object=new JsonObject();
        object.put("username","test");
        object.put("pwd","test");
        object.put("role","r");
        DBUtils.insert("user",object).onSuccess(ar->{
            System.out.println(DBUtils.toJsonArray(ar));
        }).onComplete(ar->{
            System.out.println(ar.result());
        }).onFailure(Throwable::printStackTrace);
    }
    @AfterAll
    static void sleep() throws InterruptedException {
        Thread.sleep(1500);
    }


}
