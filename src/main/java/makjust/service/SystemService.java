package makjust.service;

import io.vertx.core.json.JsonObject;

import java.net.UnknownHostException;

public interface SystemService {
    JsonObject getSystemInfo() throws UnknownHostException;
    JsonObject getCpuUsage();
}
