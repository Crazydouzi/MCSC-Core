package makjust.dao.impl;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.utils.DBUtils;

public class MCVersionDaoImpl implements makjust.dao.MCVersionDao {

    @Override
    public Future<RowSet<Row>> selectServerByEnable(boolean flag) {
        return  DBUtils.executeRowSQL("select * from mc_server where enable=?",flag);
    }
}
