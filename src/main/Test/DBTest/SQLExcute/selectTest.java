package DBTest.SQLExcute;

import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.SqlTemplate;
import makjust.entity.MCServer;
import makjust.entity.MCSetting;
import makjust.utils.DBPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class selectTest {
    static {
        Vertx vertx = Vertx.vertx();
        DBPool.conn(vertx);
    }

    @Test
    void syncSQLTest() throws InterruptedException {
        MCSetting mcSetting=new MCSetting();
        mcSetting.setId(1);
        SqlTemplate.forQuery(DBPool.getPool(),"select * from mc_setting where id=#{id}").mapFrom(MCSetting.class).mapTo(Row::toJson).execute(mcSetting).onSuccess(ar->{

            System.out.print(mcSetting);
        });
    }


    @AfterAll
    static void sleep() throws InterruptedException {
        Thread.sleep(1000);
    }
}
