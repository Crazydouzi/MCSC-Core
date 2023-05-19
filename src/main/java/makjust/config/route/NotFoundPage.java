package makjust.config.route;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class NotFoundPage {
    Router router;
    NotFoundPage(Router router){
        this.router=router;
    }
    public Router setNotFoundRouter(String excludeRegex){
        router.errorHandler(404, ctx -> {
            if (ctx.request().method() == HttpMethod.GET && ctx.request().uri().matches( excludeRegex)) {
                ctx.reroute("/index.html");
            } else {
                ctx.response().setStatusCode(404);
                ctx.json(new JsonObject().put("code", 404).put("msg", "页面走丢了哟~"));
            }
        });
        return  router;
    }

}
