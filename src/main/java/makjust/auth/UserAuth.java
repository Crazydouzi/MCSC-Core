package makjust.auth;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Arrays;

public class UserAuth {
    public void auth(RoutingContext ctx,String... urls){
        System.out.println(ctx.session().id());
        boolean flag =false;
        for (String url:urls){
            if (ctx.request().uri().contains(url)){
                flag=true;
                break;
            }
        }
        if (flag) {
            ctx.next();
        } else if (!ctx.session().isEmpty()) {
            ctx.next();
        } else {
            ctx.response().setStatusCode(405);
            ctx.json(new JsonObject().put("code", 400).put("msg", "权限验证失败，请先登录"));
        }
    }

}
