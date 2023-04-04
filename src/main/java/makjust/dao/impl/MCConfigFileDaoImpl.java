package makjust.dao.impl;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.dao.MCConfigFileDao;
import makjust.utils.DBPool;

public class MCConfigFileDaoImpl implements MCConfigFileDao {
    @Override
    public Future<RowSet<Row>> getAllConfigFile() {
        return DBPool.executeRowSQL("select file_name,description from mc_config_file");
    }
}
