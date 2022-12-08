package makjust.route;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.RoutePath;

@RoutePath("/system")
public class SystemRoute {
    //    设置远程仓库地址
    public RoutingContext setRemoteURI(RoutingContext ctx){
        return ctx;
    }
    // 重置系统
    public JsonObject  resetSystem(){
        return new JsonObject();
    }

}
