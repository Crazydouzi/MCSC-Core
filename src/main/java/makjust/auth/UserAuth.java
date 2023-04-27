package makjust.auth;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Arrays;

public class UserAuth {
    RoutingContext ctx;

    public UserAuth(RoutingContext ctx) {
        this.ctx = ctx;
    }

    public void auth(String... url) {
        if (Arrays.asList(url).contains(ctx.request().uri())) {
            ctx.next();
        } else if (!ctx.session().isEmpty()) {
            ctx.next();
        } else {
            ctx.json(new JsonObject().put("code", 400).put("msg", "权限验证失败，请先登录"));
        }
    }

}
