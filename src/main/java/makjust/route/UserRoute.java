package makjust.route;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.HttpMethod;
import makjust.annotation.RequestBody;
import makjust.annotation.Request;
import makjust.annotation.RoutePath;
import makjust.entity.User;
import makjust.service.UserService;
import makjust.service.impl.UserServiceImpl;

@RoutePath("/user")
public class UserRoute extends AbstractRoute{
    private UserService userService=new UserServiceImpl();
    // 用户登录
    @Request(value = "/userLogin",method = HttpMethod.POST,async = true)
    public RoutingContext userLogin(@RequestBody  User user, RoutingContext ctx,Vertx vertx) {
        System.out.println(user);
        userService.findUser(vertx,ar->{
            User u= Json.decodeValue(ar.result().toString(),User.class);
            System.out.println(u);
            ctx.session().put("User",u);
            ctx.response().setStatusCode(200);
            ctx.json(returnJson(200,"登录成功"));
        });
        return ctx;
    }
    // 修改用户信息
    @Request(value = "/userUpdate",method = HttpMethod.POST,async = true)
    public RoutingContext userUpdate(Vertx vertx,RoutingContext ctx) {
        userService.modifyUser(vertx,ar->{
            ctx.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200).end(ar.result().encode());
        });

        return ctx;
    }
    // 登出
    @Request(value = "/userLogout",method = HttpMethod.POST)
    public JsonObject userLogout(RoutingContext ctx){
        User user=ctx.session().get("User");
        if (user!=null){
            ctx.session().remove("User");
            return returnJson(200,"退出成功");
        }
        return returnJson(200,"您还没有登录");

    }
    @Request(value = "/asyncFindUser",method = HttpMethod.POST,async = true)
    public RoutingContext findUser(@RequestBody  User user,Vertx vertx,RoutingContext ctx){
        userService.findUser(vertx,ar->ctx.json(returnJson(200,"成功",ar.result())));
        return ctx;
    }
    // 远程认证
    public JsonObject RemoteAuth(@RequestBody  User user){
        return new JsonObject();
    }

}
