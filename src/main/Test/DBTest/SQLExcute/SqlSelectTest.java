package DBTest.SQLExcute;

import io.vertx.core.Vertx;
import makjust.utils.DBPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SqlSelectTest {
    static {
        Vertx vertx = Vertx.vertx();
        DBPool.conn(vertx);
    }

    @Test
    void strTest() {
        String key = "set java_version=#{javaVersion},mem_min=#{memMin},mem_max=#{memMax},vm_option=#{vmOption} where";
        Matcher matcher = Pattern.compile("[A-z0-9.]_([A-z0-9.])").matcher(key);
        StringBuilder sb = new StringBuilder(key);
        while (matcher.find()) {
            int index=sb.indexOf("_");
            sb.replace(index, index + 2, String.valueOf(sb.charAt(index + 1)).toUpperCase());
        }
        System.out.println(sb);
    }

    @AfterAll
    static void sleep() throws InterruptedException {
        Thread.sleep(1000);
    }
}
