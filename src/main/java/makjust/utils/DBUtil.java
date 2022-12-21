package makjust.utils;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    //执行自定义SQL
    public Future<RowSet<Row>> executeRowSQL(String sql){
        if (sql == null || sql.isEmpty()) {
            return Future.failedFuture("Query is null or empty");
        }
        if (!sql.endsWith(";")) {
            sql = sql + ";";
        }
        return pool.preparedQuery(sql).execute();
    }
    //简单查询
    public Future<RowSet<Row>> executeRowSQL(String query, Object... param) {
        if (query == null || query.isEmpty()) {
            return Future.failedFuture("Query is null or empty");
        }
        if (!query.endsWith(";")) {
            query = query + ";";
        }
        if (param.length == 0){
            return pool.preparedQuery(query).execute();
        }else {
            List<Object> params = new ArrayList<>(Arrays.asList(param));
            return pool.preparedQuery(query).execute(Tuple.tuple(params));
        }
    }
    //简单更新
    public Future<RowSet<Row>> update(String table, JsonObject param) {
        if (table == null || table.isEmpty()) {
            return Future.failedFuture("Query is null or empty");
        }
        String key= param.getMap().keySet().toString().replace("[","(").replace("]",")").replace(" ","");
        System.out.println(key);
        String query="update"+table+" set "+key+" VALUES "+key.replaceAll("([A-Za-z0-9]+)\\b","?");
        return pool.preparedQuery(query).execute(Tuple.tuple(Arrays.asList(param.getMap().keySet().toArray())));
    }
    //简单插入
    public Future<RowSet<Row>> insert(String table, JsonObject param) {
        if (table == null || table.isEmpty()) {
            return Future.failedFuture("Query is null or empty");
        }
        String key= param.getMap().keySet().toString().replace("[","(").replace("]",")").replace(" ","");
        System.out.println(key);
        String query="insert into "+table+" "+key+" VALUES "+key.replaceAll("([A-Za-z0-9]+)\\b","?");
        return pool.preparedQuery(query).execute(Tuple.tuple(new ArrayList<>(param.getMap().values())));
    }
}
