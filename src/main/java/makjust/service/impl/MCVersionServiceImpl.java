package makjust.service.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import makjust.dao.MCVersionDao;
import makjust.dao.impl.MCVersionDaoImpl;
import makjust.service.MCVersionService;
import makjust.utils.SysConfig;

import java.util.List;

public class MCVersionServiceImpl implements MCVersionService {
    private final String DIR = SysConfig.getCorePath("/");
    private final MCVersionDao mcVersionDao = new MCVersionDaoImpl();

    @Override
    public void getVersionList(Handler<AsyncResult<JsonObject>> resultHandler) {
        mcVersionDao.selectMCServerList().onSuccess(ar -> {
            if (ar!=null){
                JsonArray jsonArray=new JsonArray();
                ar.forEach(r->jsonArray.add(r.toJson()));
                resultHandler.handle(Future.succeededFuture(new JsonObject().put("data",jsonArray)));
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
}
