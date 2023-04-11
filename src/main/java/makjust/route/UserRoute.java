package makjust.route;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.*;
import makjust.pojo.User;
import makjust.service.UserService;
import makjust.service.impl.UserServiceImpl;

@RoutePath("/user")
public class UserRoute extends AbstractRoute{
    private final UserService userService=new UserServiceImpl();
    // 用户登录
    @Request(value = "/userLogin",method = HttpMethod.POST)
    public RoutingContext userLogin(RoutingContext ctx,@JsonData User user) {
        userService.userLogin(vertx, user, ar -> {
            if (ar.result().getString("msg").equals("登录成功")) {
                ctx.response().setStatusCode(200);
                if (ctx.session().get("User")==null) {
                    ctx.session().put("User", user);
                    ctx.json(returnJson(200, "登录成功"));
                }else {
                    ctx.json(returnJson(200, "请勿重复登录"));
                }
            } else {
                ctx.response().setStatusCode(200);
                ctx.json(returnJson(200, ar.result().getString("msg")));
            }
        });
        return ctx;
    }
    // 修改用户信息
    @Request(value = "/userUpdate",method = HttpMethod.POST)
    public RoutingContext userUpdate(RoutingContext ctx,@JsonData User user) {
        userService.modifyUser(vertx,user,ar-> ctx.json(returnJson(200, ar.result())));
        return ctx;
    }
    // 登出
    @Request(value = "/userLogout",method = HttpMethod.POST,async = false)
    public JsonObject userLogout(RoutingContext ctx) {
        User user=ctx.session().get("User");
        if (user!=null){
            ctx.session().remove("User");
            return returnJson(200,"退出成功");
        }
        return returnJson(200,"您还没有登录");

    }

    // session认证
    @Request(value = "/userAuth", method = HttpMethod.POST,async = false)
    public JsonObject sessionAuth(RoutingContext ctx,@JsonData User user) {
        User u = ctx.session().get("User");
        return new JsonObject().put("data", u.equals(user));
    }
    // 远程认证
    public JsonObject RemoteAuth(@JsonData User user){
        return new JsonObject();
    }

}
