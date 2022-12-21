package DBTest;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Tuple;
import makjust.entity.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLTest {
    @Test
    void select_toSQLTest(){
        JsonObject param=new JsonObject();
        param.put("user","11");
        param.put("pwd","ww");
        param.put("pwd1","ww");
        param.put("pwdf","ww");
        param.put("pwd3","ww");
        param.put("pwd2","ww");
        String sql = "select * from #{user} where pwd=#{pwd}";
        Pattern pattern = Pattern.compile("#\\{\\w*}");
        Matcher matcher = pattern.matcher(sql);
        List<String> keyList = new ArrayList<>();
        List<Object> po = new ArrayList<>();
        while (matcher.find()) {
            keyList.add(matcher.group().replaceAll("[#{}]", ""));
        }
        sql=sql.replaceAll("#\\{\\w*}","?");
        keyList.forEach(v -> {
            po.add(param.getString(v));
        });
        System.out.println(sql + "  " + po);
    }

    @Test
    void update_toSQLTest() {
        String table="table";
        User u = new User();
        u.setId(1);
        u.setPwd("2222");
        StringBuilder sql = new StringBuilder("update " + table + " set ");
        JsonObject object=JsonObject.mapFrom(u);
        Set<String> keySet=object.getMap().keySet();
        List<Object> p=new ArrayList<>(object.getMap().values());
        for (String key:keySet){
            sql.append(key).append("=?").append(",");
        }
        sql.replace(sql.lastIndexOf(","),sql.lastIndexOf(",")+1,";");
        System.out.println(p);
        System.out.println(sql);

    }
}
