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
import java.util.Set;
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
        if (!sql.endsWith(";")) {
            sql = sql + ";";
        }
        Pattern pattern = Pattern.compile("#\\{\\w*}");
        Matcher matcher = pattern.matcher(sql);
        List<String> keyList = new ArrayList<>();
        List<Object> tupleList = new ArrayList<>();
        while (matcher.find()) {
            keyList.add(matcher.group().replaceAll("[#{}]", ""));
        }
        sql = sql.replaceAll("#\\{\\w*}", "?");
        // 布尔值对sqlite 0/1转换
        keyList.forEach(v -> {
            if (param.getString(v).equals("false") || param.getString(v).equals("False")) {
                tupleList.add(0);
            } else if (param.getString(v).equals("True") || param.getString(v).equals("true")) {
                tupleList.add(1);
            } else {
                tupleList.add(param.getString(v));

            }
        });
        System.out.println("sql:" + sql + "param:" + param);
        return pool.preparedQuery(sql).execute(Tuple.tuple(tupleList));
    }

    public static Future<RowSet<Row>> executeSQL(String sql, Object param) {
        return executeSQL(sql, JsonObject.mapFrom(param));
    }

    //用于更新前检查数据，去空添加
    public static Future<RowSet<Row>> update(String sql, JsonObject param) {
        if (checkEmpty(sql)) {
            return Future.failedFuture("Query is null or empty");
        }
        Set<String> keySet = param.getMap().keySet();
        StringBuilder sqlBuilder = new StringBuilder(sql.substring(0, sql.indexOf(" set")) + " set ");
        Pattern pattern = Pattern.compile("([A-Za-z0-9._]+=#\\{[A-Za-z0-9.]+})");
        Matcher matcher = pattern.matcher(sql.substring(sql.indexOf("set"), sql.indexOf("where")));
        while (matcher.find()) {
            String key = DBPool.toCamelCase(matcher.group().replaceAll("=#\\{[A-Za-z0-9.]+}", ""));
            if (keySet.contains(key)) {
                sqlBuilder.append(sql, sql.indexOf(matcher.group()), sql.indexOf(matcher.group()) + matcher.group().length());
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.replace(sqlBuilder.lastIndexOf(","), sqlBuilder.length(), " ");
        sqlBuilder.append(sql, sql.indexOf("where"), sql.length());
        return DBPool.executeSQL(sqlBuilder.toString(), param);
    }

    public static Future<RowSet<Row>> update(String sql, Object param) {
        return update(sql, JsonObject.mapFrom(param));
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
                    nameList[1] = toSnakeCase(nameList[1]);
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
                }

                else {// 当无.时说明为普通映射
                    if (!pojo.containsKey("data")) {
                        if (!(rowSet.size() == 1)) {
                            pojo.put("data", new JsonArray());
                        }
                    }
                    if (rowSet.size() == 1) {
                        pojo.put(mapping.getString(key), row.getValue(key));
                        continue;
                    }
                    if (flag) {
                        //当位于当前结果集第一条时候
                        pojo.getJsonArray("data").add(new JsonObject().put(mapping.getString(key), row.getValue(key)));
                    } else {
                        pojo.getJsonArray("data").getJsonObject(rowIndex).put(mapping.getString(key), row.getValue(key));
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
     * mapping自动驼峰映射
     */
    public static JsonObject camelMapping(RowSet<Row> rowSet){
        if (rowSet==null)return new JsonObject("data");
        JsonObject object=new JsonObject();
        rowSet.forEach(row->{
            row.toJson().getMap().keySet().forEach(key-> object.put(key,toCamelCase(key)));
            return ;
        });
        System.out.println(object);
        return mapping(object,rowSet);
    }

    /**
     * 将驼峰转为下划线
     */
    private static String toSnakeCase(String key) {
        Matcher matcher = Pattern.compile("[A-Z]").matcher(key);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    /**
     * @param key 单个字符串
     * 将下划线转为驼峰
     */
    private static String toCamelCase(String key) {
        if (!key.contains("_")) return key;
        StringBuilder sb = new StringBuilder(key);
        int index = sb.indexOf("_");
        sb.replace(index, index + 2, String.valueOf(sb.charAt(index + 1)).toUpperCase());
        return sb.toString();
    }
    /**
     * @param str 整句转换
     * 将下划线转为驼峰
     */
    private static String strToCameCase(String str){
        Matcher matcher = Pattern.compile("[A-z0-9.]_([A-z0-9.])").matcher(str);
        StringBuilder sb = new StringBuilder(str);
        while (matcher.find()) {
            int index=sb.indexOf("_");
            sb.replace(index, index + 2, String.valueOf(sb.charAt(index + 1)).toUpperCase());
        }
        return str;
    }
}
