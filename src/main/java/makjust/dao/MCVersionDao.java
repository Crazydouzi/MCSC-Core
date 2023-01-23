package makjust.dao;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public interface MCVersionDao {
    Future<RowSet<Row>> selectMCServerList();
}
