package makjust.dao;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.entity.MCServer;

public interface MCServerDao {
    Future<RowSet<Row>> getSettingById(Integer id);
}
