package makjust.route;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.*;
import makjust.bean.User;
import makjust.service.UserService;
import makjust.service.impl.UserServiceImpl;

import java.util.Date;
import java.util.UUID;

@RoutePath("/user")
public class UserRoute extends AbstractRoute {
    private final UserService userService = new UserServiceImpl();

    // 用户登录
    @Request(value = "/userLogin", method = HttpMethod.POST)
    public RoutingContext userLogin(RoutingContext ctx, @JsonData User user) {
        userService.userLogin(user, ar -> {
            if (ar.succeeded()) {
                User u = ar.result();
                String sessionName = "user-" + u.getId();
                if (ctx.session().get(sessionName) == null) {
                    ctx.session().put(sessionName, u);
                }
                ctx.json(returnJson(200, "登录成功", ar.result()));
            } else {
                ctx.json(returnJson(345, ar.cause().getMessage()));
            }
        });
        return ctx;
    }

    // 登出
    @Request(value = "/userLogout", method = HttpMethod.POST)
    public RoutingContext userLogout(RoutingContext ctx) {
        if (!ctx.session().isEmpty()) {
            ctx.session().destroy();
        }
        if (ctx.session().isDestroyed() || ctx.session().isEmpty()) {
            ctx.json(returnJson(200, "退出成功"));
        } else {
            ctx.json(returnJson(200, "登出失败"));
        }
        return ctx;
    }

    //修改密码
    @Request(value = "/resetPwd", method = HttpMethod.POST)
    public RoutingContext rePwd(RoutingContext ctx, @JsonData("user") User user) {
        if (user != null) {
            User user1 = new User();
            user1.setId(user.getId());
            user1.setPwd(user.getPwd());
            userService.modifyUserPwd(user1, ar -> {
                if (ar.result()) {
                    ctx.json(returnJson(200, "更新成功！"));
                    ctx.session().remove("user-code-" + user.getUsername());
                } else {
                    ctx.json(returnJson(200, "更新失败！"));
                }
            });
        }
        return ctx;
    }

    // 忘记密码
    @Request(value = "/forget", method = HttpMethod.POST)
    public RoutingContext forget(RoutingContext ctx, @JsonData("user") User user, @JsonData("code") String code) {
        if (user != null) {
            if (code.equals(ctx.session().get("user-code-" + user.getUsername()))) {
                userService.modifyUserPwd(user, ar -> {
                    if (ar.result()) {
                        ctx.json(returnJson(200, "更新成功！"));
                        ctx.session().remove("user-code-" + user.getUsername());
                    } else {
                        ctx.json(returnJson(200, "更新失败！"));
                    }
                });
            } else {
                ctx.json(returnJson(400, "CODE错误！"));
            }
        }
        return ctx;
    }

    //申请CODE
    @Request(value = "/getCode", method = HttpMethod.GET)
    public RoutingContext generateCode(RoutingContext ctx, @RequestParam User user) {
        if (user != null) {
            userService.getUser(user, ar -> {
                if (ar.succeeded()) {
                    if (ar.result() != null) {
                        String code = UUID.nameUUIDFromBytes((user.getUsername() + new Date().getTime()).getBytes()).toString().split("-")[0];
                        ctx.session().put("user-code-" + user.getUsername(), code);
                        ctx.json(returnJson(200, "请前往控制台查看CODE"));
                        System.out.println("用户申请的CODE为：" + code);
                        System.out.println(ctx.session().data());

                    } else {
                        ctx.json(returnJson(400, "请求异常"));
                    }
                } else {
                    ctx.json(returnJson(500, ar.cause()));
                }
            });
        } else {
            ctx.json(returnJson(400, "参数不完整！"));
        }
        return ctx;
    }

    // 远程认证
    public JsonObject RemoteAuth() {
        return new JsonObject();
    }

}
