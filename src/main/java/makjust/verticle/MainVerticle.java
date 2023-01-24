package makjust.verticle;

import io.vertx.core.AbstractVerticle;
import makjust.annotation.Deploy;
import makjust.route.AbstractRoute;
import makjust.utils.RouteUtils;
import makjust.utils.SysConfig;

@Deploy
public class MainVerticle extends AbstractVerticle {
    @Override
    public void start(){
        AbstractRoute.vertx = vertx;
        //扫描路由
        RouteUtils routeUtils = new RouteUtils(vertx);
        routeUtils.scanRoute("makjust.route");
        routeUtils.createLocalSession();
        if ((Boolean) SysConfig.getConf("enWeb")) routeUtils.setStaticRoute(SysConfig.getStaticPath());
        routeUtils.mountAllRoute("/api/*","/ws/*");
        vertx.createHttpServer().requestHandler(routeUtils.getRouter()).listen(8080);

    }
}
