package makjust.service.impl;

import io.vertx.core.*;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import makjust.dao.MCServerDao;
import makjust.dao.impl.MCServerDaoImpl;
import makjust.entity.MCServer;
import makjust.service.MCVersionService;
import makjust.utils.DBPool;
import makjust.utils.EnvOptions;
import makjust.utils.SysConfig;

import java.util.List;

public class MCVersionServiceImpl implements MCVersionService {
    private final String DIR = SysConfig.getCorePath("/");
    private final MCServerDao mcServerDao = new MCServerDaoImpl();

    @Override
    public void getVersionList(Handler<AsyncResult<JsonObject>> resultHandler) {
        mcServerDao.selectMCServerList().onComplete(ar -> {
            if (ar != null) {
                resultHandler.handle(Future.succeededFuture(DBPool.camelMapping(ar.result())));
            }
        });

    }

    @Override
    public JsonObject getStoreVersionList() {
        return new JsonObject();
    }

    @Override
    public void serverScanner(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler) {
        FileSystem fileSystem = vertx.fileSystem();
        JsonArray jsonArray = new JsonArray();
        fileSystem.readDir(DIR, "[^.]*").onSuccess(dirList -> {
            for (String loc : dirList) {
                List<String> jarList = fileSystem.readDirBlocking(loc, "\\w*.jar");
                // 空文件夹不返回
                if (!jarList.isEmpty()) {
                    jarList.replaceAll(f -> f.substring(f.lastIndexOf("\\") + 1));
                    jsonArray.add(new JsonObject().put(loc.substring(loc.lastIndexOf("\\") + 1), jarList));
                }
            }
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", jsonArray)));

        });
    }

    @Override
    public void changeEnableVersion(MCServer server, Handler<AsyncResult<JsonObject>> resultHandler) {
        server.setEnable(true);
        boolean serverStatus = EnvOptions.getServerStatus();
        if (serverStatus) {
            resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "服务器运行中！无法切换版本")));
        } else {
            mcServerDao.updateMCServerEnable(server).onSuccess(ar -> {
                if (ar.rowCount() >= 1) {
                    resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "切换成功")));
                } else {
                    resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "切换失败")));
                }
            }).onFailure(Throwable::printStackTrace);
        }

    }
}
