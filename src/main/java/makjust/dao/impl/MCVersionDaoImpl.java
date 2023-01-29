package makjust.dao.impl;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import makjust.entity.MCServer;
import makjust.utils.DBPool;

public class MCVersionDaoImpl implements makjust.dao.MCVersionDao {

    @Override
    public Future<RowSet<Row>> selectMCServerList() {
        return  DBPool.executeRowSQL("select * from mc_server;");
    }

    @Override
    public Future<RowSet<Row>> updateMCServerEnable(MCServer server) {

//        return  DBPool.executeRowSQL("update mc_server set enable=0;").onComplete(ar->{
//            DBPool.executeSQL("update mc_server set enable=#{enable} where id=#{id};",server);
////        });
//        return DBPool.getPool().withTransaction(client -> client
//                        .preparedQuery("update mc_server set enable=0;")
//                        .execute()
//                        .flatMap(res -> client
//                                        .preparedQuery("update mc_server set enable=? where id=?;")
//                                .execute(Tuple.of(server.getEnable(),server.getId()))
//                                ));
        return DBPool.executeSQL("update mc_server set enable=#{enable} where id=#{id};",server);
    }
}