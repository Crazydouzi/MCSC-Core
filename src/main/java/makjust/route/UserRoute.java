package makjust.route;

import io.vertx.core.http.Cookie;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.HttpMethod;
import makjust.annotation.JsonData;
import makjust.annotation.Request;
import makjust.annotation.RoutePath;
import makjust.pojo.User;
import makjust.service.UserService;
import makjust.service.impl.UserServiceImpl;

@RoutePath("/user")
public class UserRoute extends AbstractRoute {
    private final UserService userService = new UserServiceImpl();

    // 用户登录
    @Request(value = "/userLogin", method = HttpMethod.POST)
    public RoutingContext userLogin(RoutingContext ctx, @JsonData User user) {
        userService.userLogin(user, ar -> {
            if (ar.succeeded()) {
                User u = ar.result().getJsonObject("data").mapTo(User.class);
                String sessionName = "user-" + u.getId();
                if (ctx.session().get(sessionName) == null) {
                    ctx.session().put(sessionName, u);
                    ctx.response().addCookie(Cookie.cookie("ssid", u.getId().toString()));
                    ctx.json(returnJson(200,ar.result()));
                } else {
                    ctx.json(returnJson(201, "请勿重复登录"));
                }
            } else {
//                ctx.response().setStatusCode(500);
                ctx.json(returnJson(345, ar.cause().getMessage()));
            }
        });
        return ctx;
    }

    // 修改用户信息
    @Request(value = "/userUpdate", method = HttpMethod.POST)
    public RoutingContext userUpdate(RoutingContext ctx, @JsonData User user) {
        userService.modifyUser( user, ar -> ctx.json(returnJson(200, ar.result())));
        return ctx;
    }

    // 登出
    @Request(value = "/userLogout", method = HttpMethod.POST)
    public RoutingContext userLogout(RoutingContext ctx, @JsonData User user) {
//        String sessionName = "user-" + user.getId();
        if (!ctx.session().isEmpty()) {
            ctx.session().destroy();
        }
        if (ctx.session().isDestroyed()) {
            ctx.json(returnJson(200, "退出成功"));
        } else {
            ctx.json(returnJson(200, "登出失败"));
        }
        return ctx;
    }

    // session认证
    @Request(value = "/userAuth", method = HttpMethod.POST, async = false)
    public JsonObject sessionAuth(RoutingContext ctx, @JsonData User user) {
        String sessionName = "user-" + user.getId();
        User u = ctx.session().get(sessionName);
        return new JsonObject().put("data", u.equals(user));
    }

    // 远程认证
    public JsonObject RemoteAuth(@JsonData User user) {
        return new JsonObject();
    }

}
