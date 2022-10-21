package makjust.controller;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class AuthController {
    public JsonObject userLogin(RoutingContext ctx) {
        String username = ctx.request().getParam("username");
        String pwd = ctx.request().getParam("pwd");
        System.out.println(username + ":" + pwd);
        return new JsonObject().put(username, pwd);
    }

    public JsonObject userRegister(RoutingContext ctx) {
        String username = ctx.request().getParam("username");
        String pwd = ctx.request().getParam("pwd");
        System.out.println(username + ":" + pwd);
        return new JsonObject().put(username, pwd);
    }

}
