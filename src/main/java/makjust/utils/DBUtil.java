package makjust.utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;

public class DBUtil {
    private static Vertx vertx;
    final JsonObject config = new JsonObject()
            .put("url", "jdbc:sqlite:" + SysConfig.resourcesPath() + "core.db")
            .put("driver_class", "org.sqlite.JDBC")
            .put("max_pool_size", 30);
    private JDBCPool pool = JDBCPool.pool(vertx, config);
    public DBUtil(Vertx vertx){
        DBUtil.vertx =vertx;
    }
    public JDBCPool Pool(){
        return pool;
    }
}
