package makjust.service;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import makjust.dto.SystemConfigDTO;

import java.net.UnknownHostException;

public interface SystemService {
    JsonObject getSystemInfo() throws UnknownHostException;

    JsonObject getCpuUsage();

    JsonObject getMemoryUsage();
    JsonObject getSystemConfig();
    JsonObject saveSystemConfig(Vertx vertx,SystemConfigDTO config);
}
