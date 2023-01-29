package makjust.dao.impl;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.utils.DBPool;

public class MCVersionDaoImpl implements makjust.dao.MCVersionDao {

    @Override
    public Future<RowSet<Row>> selectMCServerList() {
        return  DBPool.executeRowSQL("select * from mc_server;");
    }
}
