package makjust.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import makjust.entity.MCServer;

public interface MCVersionService {
    //获取已装版本
    void getVersionList(Handler<AsyncResult<JsonObject>> resultHandler);
    //获取远程库列表
    JsonObject getStoreVersionList();
    //文件扫描器
    void serverScanner(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler);
    //切换启用版本
    void changeEnableVersion(MCServer server,Handler<AsyncResult<JsonObject>> resultHandler);
}
