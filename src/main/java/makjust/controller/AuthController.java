package makjust.controller;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.HttpMethod;
import makjust.annotation.RequestBody;
import makjust.annotation.RequestMapping;
import makjust.annotation.RestController;
import makjust.pojo.User;

@RestController
public class AuthController {
    @RequestMapping(value = "/userLogin",method = HttpMethod.POST)
    public JsonObject userLogin(@RequestBody  User user, RoutingContext ctx) {
        return JsonObject.mapFrom(user);
    }
    @RequestMapping(value = "/userRegister",method = HttpMethod.POST)
    public JsonObject userRegister(@RequestBody  User user) {
        return JsonObject.mapFrom(user);
    }

}
