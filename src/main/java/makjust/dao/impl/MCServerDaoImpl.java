package makjust.dao.impl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.dao.MCServerDao;
import makjust.utils.DBPool;

public class MCServerDaoImpl implements MCServerDao {
    @Override
    public Future<RowSet<Row>> getSettingById(Integer id) {
        return DBPool.executeSQL("select option,value,enable from mc_setting where server_id=#{id}",new JsonObject().put("id",id));
    }
    //只查询一条 避免出现问题
    @Override
    public Future<RowSet<Row>> selectServerByEnable(boolean flag) {
        return  DBPool.executeRowSQL("select * from mc_server where enable=? LIMIT 1",flag);
    }
}
