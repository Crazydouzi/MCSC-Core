package makjust.config.auth;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class UserAuth {
    private String regex="(?!/(api|ws))/.*";
    public void auth(RoutingContext ctx,String... urls){
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
            if (ctx.request().method() == HttpMethod.GET && ctx.request().uri().matches(regex)){
                ctx.reroute("/index.html");
            }else {
                ctx.response().setStatusCode(405);
                ctx.json(new JsonObject().put("code", 400).put("msg", "权限验证失败，请先登录"));
            }
        }
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
