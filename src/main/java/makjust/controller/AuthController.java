package makjust.controller;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.HttpMethod;
import makjust.annotation.RequestBody;
import makjust.annotation.RequestMapping;
import makjust.annotation.Controller;
import makjust.pojo.User;

@Controller("/core")
public class AuthController {
    @RequestMapping(value = "/userLogin",method = HttpMethod.POST)
    public JsonObject userLogin(@RequestBody  User user, RoutingContext ctx) {
        return JsonObject.mapFrom(user);
    }
    @RequestMapping(value = "/userRegister",method = HttpMethod.POST)
    public JsonObject userRegister(@RequestBody  User user) {
        return JsonObject.mapFrom(user);
    }
    @RequestMapping(value = "/userLogout",method = HttpMethod.POST)
    public JsonObject userLogout(@RequestBody  User user){
        return JsonObject.mapFrom(user);

    }
    public JsonObject RemoteAuth(@RequestBody  User user){
        return new JsonObject();
    }

}
