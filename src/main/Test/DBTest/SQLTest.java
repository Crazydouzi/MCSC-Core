package DBTest;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Tuple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLTest {
    @Test
    void insert_toSQLTest(){
        JsonObject param=new JsonObject();
        param.put("user","11");
        param.put("pwd","ww");
        param.put("pwd1","ww");
        param.put("pwdf","ww");
        param.put("pwd3","ww");
        param.put("pwd2","ww");
        String key= param.getMap().keySet().toString().replace("[","(").replace("]",")").replace(" ","");
        String query="insert into "+"aa"+" "+key+" VALUES "+key.replaceAll("([A-Za-z0-9]+)\\b","?");
        System.out.println(query);
    }
    @Test
    void select_toSQLTest(){
        String sql="select * from user where a=1 and b=2 or c=1";
        String regex = "where\\w"; //正则表达式
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(sql);
        List<String> matchRegexList = new ArrayList<String>();
        while(m.find()){
            matchRegexList.add(m.group());
        }
        System.out.println(matchRegexList.toString());
    }
}
