package makjust.dao;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public interface MCServerDao {
    Future<RowSet<Row>> getSettingById(Integer id);

    Future<RowSet<Row>> selectServerByEnable(boolean flag);
}
