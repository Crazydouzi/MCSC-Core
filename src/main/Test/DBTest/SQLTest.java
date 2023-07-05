package DBTest;

import io.vertx.core.json.JsonObject;
import makjust.bean.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLTest {
    @Test
    void select_toSQLTest() {
        JsonObject param = new JsonObject();
        param.put("user", "11");
        param.put("pwd", "ww");
        param.put("pwd1", "ww");
        param.put("pwdf", "ww");
        param.put("pwd3", "ww");
        param.put("pwd2", "ww");
        String sql = "select * from #{user} where pwd=#{pwd}";
        Pattern pattern = Pattern.compile("#\\{\\w*}");
        Matcher matcher = pattern.matcher(sql);
        List<String> keyList = new ArrayList<>();
        List<Object> po = new ArrayList<>();
        while (matcher.find()) {
            keyList.add(matcher.group().replaceAll("[#{}]", ""));
        }
        sql = sql.replaceAll("#\\{\\w*}", "?");
        keyList.forEach(v -> {
            po.add(param.getString(v));
        });
        System.out.println(sql + "  " + po);
    }

    @Test
    void update_toSQLTest() {
        User u = new User();
        u.setId(1);
        u.setPwd("2222");
        JsonObject object = JsonObject.mapFrom(u);
        Set<String> keySet = object.getMap().keySet();
        String baseSql= "update table set username=#{Id},pwd=#{pwd} where id=#{id};";
        StringBuilder sql = new StringBuilder(baseSql.substring(0,baseSql.indexOf(" set")) + " set ");
//        List<Object> p = new ArrayList<>(object.getMap().values());
//        for (String key : keySet) {
//                if (baseSql.substring(baseSql.lastIndexOf("set"),baseSql.lastIndexOf("where")).contains(key)){
//                    sql.append(key).append("=?").append(",");
//                }
//        }

        Pattern pattern = Pattern.compile("[A-Za-z0-9.]+=#\\{[A-Za-z0-9.]+}");
        Matcher matcher = pattern.matcher(baseSql.substring(baseSql.indexOf(" set "),baseSql.indexOf("where")));
        while (matcher.find()) {
            System.out.println(matcher.group());
            System.out.println(baseSql.indexOf(matcher.group())+"   "+(baseSql.indexOf(matcher.group())+matcher.group().length()-1));
            String key=matcher.group().replaceAll("=#\\{[A-Za-z0-9.]+}","");
            if (keySet.contains(key)){
//                    sql.append(key).append("=?").append(",");
                sql.append(baseSql, baseSql.indexOf(matcher.group()), baseSql.indexOf(matcher.group())+matcher.group().length());
            }

        }
        sql.append(" ");
//        sql.replace(sql.lastIndexOf(","), sql.length()-1, " ");
        sql.append(baseSql, baseSql.indexOf("where"), baseSql.length());
        System.out.println(sql);


    }
    @Test
    void update_test(){
        String sql = "update " + "QAQ" + " set ";
        Pattern pattern = Pattern.compile("#\\{\\w*}");
        Matcher matcher = pattern.matcher(sql);
        List<Object> keyList = new ArrayList<>();
        while (matcher.find()) {
            keyList.add(matcher.group().replaceAll("[#{}]", ""));
        }
        sql = sql.replaceAll("#\\{\\w*}", "?");
        System.out.print("sql");
        System.out.println(keyList);

    }

}
