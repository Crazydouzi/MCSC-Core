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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DBPool {
    public static Vertx vertx;
    private static JDBCPool pool;
    private static final JsonObject config = new JsonObject().put("url", "jdbc:sqlite:" + SysConfig.resourcesPath() + "config/core.db").put("driver_class", "org.sqlite.JDBC").put("max_pool_size", 5);

    public static void conn(Vertx vertx) {
        DBPool.vertx = vertx;
        pool = JDBCPool.pool(vertx, config);
    }

    public static JDBCPool getPool() {
        return pool;
    }

    public static void getConn() {
        pool = JDBCPool.pool(vertx, config);
    }

    public static void close() {
        pool.close();
    }

    public static JsonArray toJsonArray(RowSet<Row> rowSet) {
        JsonArray jsonArray = new JsonArray();
        for (Row row : rowSet) {
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
        if (param.length == 0) {
            return pool.preparedQuery(query).execute();
        } else {
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
        sql = sql.replaceAll("#\\{\\w*}", "?");
        keyList.forEach(v -> {
            if (param.getString(v).equals("false") || param.getString(v).equals("False")) {
                tupleList.add(0);
            } else if (param.getString(v).equals("True") || param.getString(v).equals("true")) {
                tupleList.add(1);
            } else {
                tupleList.add(param.getString(v));

            }
        });


        return pool.preparedQuery(sql).execute(Tuple.tuple(tupleList));
    }

    public static Future<RowSet<Row>> executeSQL(String sql, Object param) {
        return executeSQL(sql, JsonObject.mapFrom(param));
    }

    //简单更新
    public static Future<RowSet<Row>> update(String table, JsonObject param) {
        if (checkEmpty(table)) {
            return Future.failedFuture("Query is null or empty");
        }
        String sql = "update " + table + " set ";
        Pattern pattern = Pattern.compile("#\\{\\w*}");
        Matcher matcher = pattern.matcher(sql);
        List<Object> keyList = new ArrayList<>();
        while (matcher.find()) {
            keyList.add(matcher.group().replaceAll("[#{}]", ""));
        }
        sql = sql.replaceAll("#\\{\\w*}", "?");
        return pool.preparedQuery(sql).execute(Tuple.tuple(keyList));
    }

    public static Future<RowSet<Row>> update(String table, Object param) {
        return update(table, JsonObject.mapFrom(param));
    }

    //简单插入
    public static Future<RowSet<Row>> insert(String table, JsonObject param) {
        if (checkEmpty(table)) {
            return Future.failedFuture("Query is null or empty");
        }
        String key = param.getMap().keySet().toString().replace("[", "(").replace("]", ")").replace(" ", "");
        String query = "insert into " + table + key + " VALUES " + key.replaceAll("([A-Za-z0-9]+)\\b", "?");
        return pool.preparedQuery(query).execute(Tuple.tuple(new ArrayList<>(param.getMap().values())));
    }

    public static Future<RowSet<Row>> insert(String table, Object param) {
        return insert(table, JsonObject.mapFrom(param));
    }

    public static boolean checkEmpty(String p) {
        return p == null || p.isEmpty();
    }

    /**
     * @param mapping key:表值 value映射值
     *                可能情况 var1->A.var1 var1->A.B.var1 or var1->A.var1 var2->B.var1
     *                {
     *                "key1":A.key1,
     *                "key2":A.key2,
     *                "key3":B:k1
     *                }
     *                return{
     *                A:[
     *                {key1:value1,key2:value2},....someObject
     *                ],
     *                B:[{k1:value3}]
     *                }
     */
    public static JsonObject mapping(JsonObject mapping, RowSet<Row> rowSet) {
        JsonObject pojo = new JsonObject();
        int rowIndex = 0;
        boolean flag = true;
        for (Row row : rowSet) {
            //这里是为了获取映射的key key为表名
            for (String key : mapping.getMap().keySet()) {
                // 获取映射变量名 pojoName为映射参数
                String pojoValue = mapping.getString(key);
                //pojo.parma
                if (pojoValue.contains(".")) {
                    //0为pojo名 1为属性名 A-var1
                    String[] nameList = pojoValue.split("\\.");
                    nameList[1] = toCase(nameList[1]);
                    if (!pojo.containsKey(nameList[0])) {
                        //如果不止一条将返回JsonArray
                        if (rowSet.size() != 1) {
                            pojo.put(nameList[0], new JsonArray());
                        } else {
                            pojo.put(nameList[0], new JsonObject());
                        }
                    }
                    //当只有一条数据时返回JsonObject
                    if (rowSet.size() == 1) {
                        pojo.getJsonObject(nameList[0]).put(nameList[1], row.getValue(key));
                        continue;
                    }
                    if (flag) {
                        //当位于当前结果集第一条时候
                        pojo.getJsonArray(nameList[0]).add(new JsonObject().put(nameList[1], row.getValue(key)));
                    } else {
                        pojo.getJsonArray(nameList[0]).getJsonObject(rowIndex).put(nameList[1], row.getValue(key));
                    }

                    //param
                } else {
                    if (!pojo.containsKey("pojo")) {
                        if (rowSet.size() == 1) {
                            pojo.put("pojo", new JsonObject());

                        } else {
                            pojo.put("pojo", new JsonArray());
                        }
                    }
                    if (rowSet.size() == 1) {
                        pojo.getJsonObject("pojo").put(toCase(pojoValue), row.getValue(key));
                        continue;
                    }
                    if (flag) {
                        //当位于当前结果集第一条时候
                        pojo.getJsonArray("pojo").add(new JsonObject().put(key, row.getValue(key)));
                    } else {
                        pojo.getJsonArray("pojo").getJsonObject(rowIndex).put(key, row.getValue(key));
                    }
                }
                flag = false;
            }
            rowIndex++;
            flag = true;
        }
        return pojo;
    }

    /**
     * 用于大写转驼峰
     */
    private static String toCase(String key) {
        Matcher matcher = Pattern.compile("[A-Z]").matcher(key);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}