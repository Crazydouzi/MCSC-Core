package makjust.dao.impl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.dao.MCSettingDao;
import makjust.entity.MCSetting;
import makjust.utils.DBPool;

public class MCSettingDaoImpl implements MCSettingDao {

    @Override
    public Future<RowSet<Row>> getSettingById(Integer id) {
        return DBPool.executeSQL("select server_id,java_version,mem_min,mem_max,vm_options from mc_setting where server_id=#{id}",new JsonObject().put("id",id));
    }

    @Override
    public Future<RowSet<Row>> updateSetting(MCSetting mcSetting) {
        return null;
    }

    @Override
    public Future<RowSet<Row>> insertSetting(MCSetting mcSetting) {
        return null;
    }
}