package DBTest.SQLExcute;

import io.vertx.core.Vertx;
import makjust.entity.MCServer;
import makjust.utils.DBPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class selectTest {
    static {
        Vertx vertx = Vertx.vertx();
        DBPool.conn(vertx);
    }

    @Test
    void syncSQLTest() throws InterruptedException {
        List<MCServer> mcServerList=new ArrayList<>();
    }

    @AfterAll
    static void sleep() throws InterruptedException {
        Thread.sleep(1000);
    }
}
