package makjust.dao.impl;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.dao.MCServerDao;
import makjust.bean.MCServer;
import makjust.utils.DBPool;

public class MCServerDaoImpl implements MCServerDao {
    //只查询一条 避免出现问题
    @Override
    public Future<RowSet<Row>> getServerByEnable(boolean flag) {
        return DBPool.executeRowSQL("select * from mc_server where enable=? LIMIT 1", flag);
    }

    @Override
    public Future<RowSet<Row>> getServerById(MCServer mcServer) {
        return DBPool.executeSQL("select * from mc_server where id=#{id} LIMIT 1", mcServer);
    }

    @Override
    public Future<RowSet<Row>> getServerLocationById(MCServer mcServer) {
        return DBPool.executeSQL("select location from mc_server where id=#{id} LIMIT 1", mcServer);

    }

    @Override
    public Future<RowSet<Row>> selectMCServerList() {
        return DBPool.executeRowSQL("select * from mc_server;");
    }

    @Override
    public Future<RowSet<Row>> updateMCServerEnable(MCServer server) {
        return DBPool.update("update mc_server set enable=#{enable} where id=#{id};", server);
    }

    @Override
    public Future<RowSet<Row>> updateMCServerInfo(MCServer mcServer) {
        return  DBPool.update("update mc_server set server_name=#{serverName},version=#{version},location=#{location} where id=#{id};", mcServer);
    }

    @Override
    public Future<RowSet<Row>> insertMCServer(MCServer mcServer) {
        return DBPool.insert("mc_server",mcServer);
    }
    public  Future<Integer> deleteMCServer(MCServer mcServer){
        return DBPool.executeSQL("delete from mc_server where id=#{id}",mcServer).compose(rows ->Future.succeededFuture(rows.rowCount()));
    };

}
