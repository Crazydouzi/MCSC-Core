package makjust.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public interface MCVersionService {
    void getVersionList(Handler<AsyncResult<JsonObject>> resultHandler);
    JsonObject getStoreVersionList();
    void serverScanner(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler);
}
