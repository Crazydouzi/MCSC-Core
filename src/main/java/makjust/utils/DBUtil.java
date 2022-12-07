package makjust.utils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class DBUtil {
    private static Vertx vertx;
    private static JDBCPool pool;
    private static JsonObject config = new JsonObject()
            .put("url", "jdbc:sqlite:" + SysConfig.resourcesPath() + "core.db")
            .put("driver_class", "org.sqlite.JDBC")
            .put("max_pool_size", 5);
    public static void conn(Vertx vertx){
        DBUtil.vertx = vertx;
        pool = JDBCPool.pool(vertx, config);
    }

    public JDBCPool getPool() {
        return pool;
    }
    public void getConn(){
        pool = JDBCPool.pool(vertx, config);
    }
    public void close(){
        pool.close();
    }
    public static JsonArray toJsonArray(RowSet<Row> rowSet){
        JsonArray jsonArray=new JsonArray();
        for (Row row:rowSet){
            jsonArray.add(row.toJson());
        }
        return jsonArray;
    }
    public Future<RowSet<Row>> query(String query, JsonArray params) {
        if (query == null || query.isEmpty()) {
            return Future.failedFuture("Query is null or empty");
        }
        if (!query.endsWith(";")) {
            query = query + ";";
        }
        Handler<AsyncResult<JsonObject>> resultHandler;
        return pool.preparedQuery(query).execute();
//        return queryResultFuture;
    }
}
