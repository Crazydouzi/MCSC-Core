package makjust.route;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.HttpMethod;
import makjust.annotation.RequestBody;
import makjust.annotation.Request;
import makjust.annotation.RoutePath;
import makjust.entity.User;
import makjust.service.UserService;
import makjust.service.impl.UserServiceImpl;

@RoutePath("/core")
public class AuthRoute {
    private UserService userService=new UserServiceImpl();
    // 用户登录
    @Request(value = "/userLogin",method = HttpMethod.POST)
    public JsonObject userLogin(@RequestBody  User user, RoutingContext ctx) {
        return JsonObject.mapFrom(user);
    }
    // 修改用户信息
//    @Request(value = "/userUpdate",method = HttpMethod.POST,async = true)
    public Handler<RoutingContext> userUpdate(Vertx vertx) {
        return ctx->{
            userService.modifyUser(vertx,ar->{
                ctx.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200).end(ar.result().encode());
            });
        };
    }
    // 登出
    @Request(value = "/userLogout",method = HttpMethod.POST)
    public JsonObject userLogout(@RequestBody  User user){
        return JsonObject.mapFrom(user);

    }
    @Request(value = "/asyncFindUser",method = HttpMethod.POST,async = true)
    public RoutingContext findUser(@RequestBody  User user,Vertx vertx,RoutingContext ctx){
        userService.findUser(vertx,ar->ctx.json(ar.result()));
        return ctx;
    }
    // 远程认证
    public JsonObject RemoteAuth(@RequestBody  User user){
        return new JsonObject();
    }

}
