package makjust.controller;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public abstract class AbstractController {
    public static Vertx vertx;
    public static Router router;
    EventBus eventBus = vertx.eventBus();

    public void sendToBus(String name, String code, JsonObject object) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("code:", code);
        jsonObject.put("data", object);
        eventBus.send(name, jsonObject);
    }

    public MessageConsumer<Object> getMsgFromBus(String name) {
        return  eventBus.consumer(name);
    }
}
