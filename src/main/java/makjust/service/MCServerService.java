package makjust.service;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.net.URISyntaxException;

public interface MCServerService {

    JsonObject editSetting();
    JsonObject getSetting();
    boolean serverStart(Vertx vertx) ;
    boolean serverStop(Vertx vertx);
}
