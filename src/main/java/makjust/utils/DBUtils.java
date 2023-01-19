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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBUtils{
    public static Vertx vertx;
    private static JDBCPool pool;
    private static final JsonObject config = new JsonObject()
            .put("url", "jdbc:sqlite:" + SysConfig.resourcesPath() + "config/core.db")
            .put("driver_class", "org.sqlite.JDBC")
            .put("max_pool_size", 5);
    public static void conn(Vertx vertx){
        DBUtils.vertx = vertx;
        pool = JDBCPool.pool(vertx, config);
    }

    public JDBCPool getPool() {
        return pool;
    }
    public static void getConn(){
        pool = JDBCPool.pool(vertx, config);
    }
    public static void close(){
        pool.close();
    }

    public static JsonArray toJsonArray(RowSet<Row> rowSet){
        JsonArray jsonArray=new JsonArray();
        for (Row row:rowSet){
            jsonArray.add(row.toJson());
        }
        return jsonArray;
    }

    //简单查询
    public static Future<RowSet<Row>> executeRowSQL(String query, Object... param) {
        if (checkEmpty(query)) {
            return Future.failedFuture("Query is null or empty");
        }
        if (!query.endsWith(";")) {
            query = query + ";";
        }
        if (param.length == 0){
            return pool.preparedQuery(query).execute();
        }else {
            return pool.preparedQuery(query).execute(Tuple.from(param));
        }
    }
    //执行自定义SQL
    public static Future<RowSet<Row>> executeSQL(String sql, JsonObject param) {
        if (checkEmpty(sql)) {
            return Future.failedFuture("Query is null or empty");
        }
        Pattern pattern = Pattern.compile("#\\{\\w*}");
        Matcher matcher = pattern.matcher(sql);
        List<String> keyList = new ArrayList<>();
        List<Object> tupleList = new ArrayList<>();
        while (matcher.find()) {
            keyList.add(matcher.group().replaceAll("[#{}]", ""));
        }
        sql=sql.replaceAll("#\\{\\w*}","?");
        keyList.forEach(v -> {
            tupleList.add(param.getString(v));
        });
        return pool.preparedQuery(sql).execute(Tuple.tuple(tupleList));
    }
    //简单更新
    public static Future<RowSet<Row>> update(String table, JsonObject param) {
        if (checkEmpty(table)) {
            return Future.failedFuture("Query is null or empty");
        }
        String sql="update "+table+" set ";
        Pattern pattern = Pattern.compile("#\\{\\w*}");
        Matcher matcher = pattern.matcher(sql);
        List<Object> keyList = new ArrayList<>();
        while (matcher.find()){
            keyList.add(matcher.group().replaceAll("[#{}]",""));
        }
        sql=sql.replaceAll("#\\{\\w*}","?");
        return pool.preparedQuery(sql).execute(Tuple.tuple(keyList));
    }
    public static Future<RowSet<Row>> update(String table, Object param){
        return  update(table, JsonObject.mapFrom(param));
    }
    //简单插入
    public static Future<RowSet<Row>> insert(String table, JsonObject param) {
        if (checkEmpty(table)) {
            return Future.failedFuture("Query is null or empty");
        }
        String key= param.getMap().keySet().toString().replace("[","(").replace("]",")").replace(" ","");
        String query="insert into "+table+key+" VALUES "+key.replaceAll("([A-Za-z0-9]+)\\b","?");
        return pool.preparedQuery(query).execute(Tuple.tuple(new ArrayList<>(param.getMap().values())));
    }
    public static Future<RowSet<Row>> insert(String table, Object param){
       return insert(table,JsonObject.mapFrom(param));
    }
     public static boolean checkEmpty(String p){
        return p == null || p.isEmpty();
    }
}
