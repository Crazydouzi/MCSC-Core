package makjust.dao;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.pojo.MCServer;
import makjust.pojo.MCSetting;

public interface MCSettingDao {
    Future<RowSet<Row>> getSettingById(Integer id);
    Future<RowSet<Row>> updateSetting(MCSetting mcSetting);
    Future<RowSet<Row>> insertSetting(MCSetting mcSetting);
    Future<Integer> deleteSetting(MCServer server);
}
