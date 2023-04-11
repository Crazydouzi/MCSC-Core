package makjust.dao;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.pojo.MCServer;

public interface MCServerDao {
    Future<RowSet<Row>> getServerByEnable(boolean flag);
    Future<RowSet<Row>> getServerById(MCServer mcServer);

    Future<RowSet<Row>> getServerLocationById(MCServer mcServer);
    Future<RowSet<Row>> selectMCServerList();
    Future<RowSet<Row>> updateMCServerEnable(MCServer mcServer);
    Future<RowSet<Row>> updateMCServerInfo(MCServer mcServer);
    Future<RowSet<Row>> insertMCServer(MCServer mcServer);

}
