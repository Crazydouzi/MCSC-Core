package DBTest.SQLExcute;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.entity.MCServer;
import makjust.utils.DBUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class selectTest {
    static {
        Vertx vertx = Vertx.vertx();
        DBUtils.conn(vertx);
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
