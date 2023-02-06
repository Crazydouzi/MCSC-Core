package makjust.service.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import makjust.dao.MCServerDao;
import makjust.dao.MCSettingDao;
import makjust.dao.impl.MCServerDaoImpl;
import makjust.dao.impl.MCSettingDaoImpl;
import makjust.entity.MCServer;
import makjust.service.MCVersionService;
import makjust.utils.EnvOptions;
import makjust.utils.SysConfig;

import java.util.List;

public class MCVersionServiceImpl implements MCVersionService {
    private final String DIR = SysConfig.getCorePath("/");
    private final MCServerDao mcServerDao=new MCServerDaoImpl();

    @Override
    public void getVersionList(Handler<AsyncResult<JsonObject>> resultHandler) {
        mcServerDao.selectMCServerList().onSuccess(ar -> {
            if (ar != null) {
                JsonArray jsonArray = new JsonArray();
                ar.forEach(r -> jsonArray.add(r.toJson()));
                resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", jsonArray)));
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
                JsonObject jsonObject = new JsonObject();
                List<String> jarList = fileSystem.readDirBlocking(loc, "\\w*.jar");
                jarList.replaceAll(f -> f.substring(f.lastIndexOf("\\") + 1));
                jsonObject.put("jarList", jarList);
                jsonArray.add(new JsonObject().put(loc.substring(loc.lastIndexOf("\\") + 1), jsonObject));
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
                System.out.println(ar.rowCount());
                if (ar.rowCount() >= 1) {
                    resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "切换成功")));
                } else {
                    resultHandler.handle(Future.succeededFuture(new JsonObject().put("data", "切换失败")));
                }
            }).onFailure(Throwable::printStackTrace);
        }

    }
}
