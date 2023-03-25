package makjust.route;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public abstract class AbstractRoute {
    public static Vertx vertx;

    public void sendToBus(String path, String code, JsonObject object) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("code:", code);
        jsonObject.put("data", object);
        vertx.eventBus().send(path, jsonObject);
    }

    private JsonObject returnJson(int statusCode, String msg, JsonObject data) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("code", statusCode);
        jsonObject.put("msg", msg);
        if (data.containsKey("data")) {
            jsonObject.put("data", data.getValue("data"));
        } else if (data.size() != 0) {
            jsonObject.put("data", data);
        } else {
            jsonObject.put("data", "");
        }
        return jsonObject;

    }

    //对于传入状态和混合值 msg?data?
    JsonObject returnJson(int statusCode, Object data) {
        return returnJson(statusCode, new JsonObject().put("data", data));
    }

    //对于传入状态码和data
    JsonObject returnJson(int statusCode, JsonObject data) {
        if (data.containsKey("msg") && data.containsKey("data")) {
            return returnJson(statusCode, data.getString("msg"), data);
        } else if (data.containsKey("msg")) {
            return returnJson(statusCode, data.getString("msg"), new JsonObject());
        } else return returnJson(statusCode, "OK", data);
    }

    //对于传入状态码和消息，没有data
    JsonObject returnJson(int statusCode, String msg) {
        return returnJson(statusCode, msg, new JsonObject());
    }

    //对于混合模式status?msg?data?
    JsonObject returnJson(JsonObject object) {
        if (object == null) {
            return returnJson(200, "OK");
        }
        if (object.containsKey("code")) {
            return returnJson(object.getInteger("code"), object);
        } else {
            return returnJson(200, object.getValue("data"));
        }
    }
}
