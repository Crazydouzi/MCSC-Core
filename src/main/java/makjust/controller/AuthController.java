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
    // 用户登录
    @RequestMapping(value = "/userLogin",method = HttpMethod.POST)
    public JsonObject userLogin(@RequestBody  User user, RoutingContext ctx) {
        return JsonObject.mapFrom(user);
    }
    // 用户注册
    @RequestMapping(value = "/userRegister",method = HttpMethod.POST)
    public JsonObject userRegister(@RequestBody  User user) {
        return JsonObject.mapFrom(user);
    }
    // 登出
    @RequestMapping(value = "/userLogout",method = HttpMethod.POST)
    public JsonObject userLogout(@RequestBody  User user){
        return JsonObject.mapFrom(user);

    }
    // 远程认证
    public JsonObject RemoteAuth(@RequestBody  User user){
        return new JsonObject();
    }

}
