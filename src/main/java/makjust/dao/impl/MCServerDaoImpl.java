package makjust.dao.impl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.dao.MCServerDao;
import makjust.utils.DBUtils;

public class MCServerDaoImpl implements MCServerDao {
    @Override
    public Future<RowSet<Row>> getSettingById(Integer id) {
        return DBUtils.executeSQL("select option,value,enable from mc_setting where server_id=#{id}",new JsonObject().put("id",id));
    }
}
