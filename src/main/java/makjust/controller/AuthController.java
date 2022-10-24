package makjust.controller;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.HttpMethod;
import makjust.annotation.RequestBody;
import makjust.annotation.RequestMapping;
import makjust.annotation.RestController;

@RestController
public class AuthController {
    @RequestMapping(value = "/userLogin",method = HttpMethod.POST)
    public JsonObject userLogin(RoutingContext ctx) {
        String username = ctx.body().asJsonObject().getString("username");
        String pwd = ctx.body().asJsonObject().getString("pwd");
        System.out.println(username + ":" + pwd);
        return new JsonObject().put(username, pwd);
    }
    @RequestMapping(value = "/userRegister",method = HttpMethod.POST)
    public JsonObject userRegister(JsonObject object) {
        String username = object.getString("username");
        String pwd = object.getString("pwd");
        System.out.println(username + ":" + pwd);
        return new JsonObject().put(username, pwd);
    }

}
