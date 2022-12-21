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

    public JsonObject returnJson(int statusCode,String msg, JsonArray data){
        JsonObject jsonObject=new JsonObject();
        jsonObject.put("code",statusCode);
        jsonObject.put("msg",msg);
        jsonObject.put("data",data);
        return jsonObject;
    }
    public JsonObject returnJson(int statusCode, JsonObject data){
        return returnJson(statusCode,"OK",data);
    }
    public JsonObject returnJson(int statusCode,String msg , JsonObject data){
        JsonObject jsonObject=new JsonObject();
        jsonObject.put("code",statusCode);
        jsonObject.put("msg",msg);
        if (data.getJsonObject("data")!=null){
            jsonObject.put("data",data.getJsonObject("data"));
        }else {
            jsonObject.put("data",data);
        }
        return jsonObject;

    }
    public JsonObject returnJson(int statusCode,String msg) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("code", statusCode);
        jsonObject.put("msg", msg);
        return jsonObject;
    }

    public MessageConsumer<Object> getMsgFromBus(String name) {
        return  vertx.eventBus().consumer(name);
    }
}
