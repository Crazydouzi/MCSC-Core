package makjust.service;

import io.vertx.core.json.JsonObject;

public interface MCVersionService {
    JsonObject getVersionList();
    JsonObject getStoreVersionList();
    JsonObject scanVersion();
}