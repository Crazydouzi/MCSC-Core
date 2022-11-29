package makjust.controller;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.HttpMethod;
import makjust.annotation.RequestBody;
import makjust.annotation.Request;
import makjust.annotation.Controller;
import makjust.entity.User;
import makjust.env.EnvOptions;

@Controller("/core")
public class AuthController {
    // 用户登录
    @Request(value = "/userLogin",method = HttpMethod.POST)
    public JsonObject userLogin(@RequestBody  User user, RoutingContext ctx) {
        return JsonObject.mapFrom(user);
    }
    // 修改用户信息
    @Request(value = "/userUpdate",method = HttpMethod.POST)
    public JsonObject userUpdate(@RequestBody  User user) {
        return JsonObject.mapFrom(user);
    }
    // 登出
    @Request(value = "/userLogout",method = HttpMethod.POST)
    public JsonObject userLogout(@RequestBody  User user){
        return JsonObject.mapFrom(user);

    }
    // 远程认证
    public JsonObject RemoteAuth(@RequestBody  User user){
        return new JsonObject();
    }

}
