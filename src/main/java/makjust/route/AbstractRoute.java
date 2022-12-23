package makjust.route;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public abstract class AbstractRoute {
    public static Vertx vertx;
    public static RoutingContext ctx;

    public void sendToBus(String path, String code, JsonObject object) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("code:", code);
        jsonObject.put("data", object);
        vertx.eventBus().send(path, jsonObject);
    }
    private JsonObject returnJson(int statusCode, String msg, JsonObject data){
        JsonObject jsonObject=new JsonObject();
        jsonObject.put("code",statusCode);
        jsonObject.put("msg",msg);
        if (data.getValue("data")!=null){
            jsonObject.put("data",data.getValue("data"));
        }else {
            jsonObject.put("data",data);
        }
        return jsonObject;

    }
    JsonObject returnJson(int statusCode, JsonObject data){
        return returnJson(statusCode,"OK",data);
    }
    JsonObject returnJson(int statusCode, String msg) {
        return returnJson(statusCode,msg,new JsonObject());
    }

    public MessageConsumer<Object> getMsgFromBus(String name) {
        return  vertx.eventBus().consumer(name);
    }
}
