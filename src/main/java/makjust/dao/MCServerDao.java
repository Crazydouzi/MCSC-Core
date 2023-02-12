package makjust.dao;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.entity.MCServer;

public interface MCServerDao {
    Future<RowSet<Row>> getServerByEnable(boolean flag);
    Future<RowSet<Row>> selectMCServerList();
    Future<RowSet<Row>> updateMCServerEnable(MCServer mcServer);
}
