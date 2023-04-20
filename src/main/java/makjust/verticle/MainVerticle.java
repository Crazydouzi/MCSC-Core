package makjust.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.handler.BodyHandler;
import makjust.annotation.Deploy;
import makjust.route.AbstractRoute;
import makjust.utils.RouteUtils;
import makjust.utils.SysConfig;

@Deploy
public class MainVerticle extends AbstractVerticle {
    @Override
    public void start(){
        System.out.println("主VERTICLE挂载中。。。。。");
        AbstractRoute.vertx = vertx;
        BodyHandler bodyHandler=BodyHandler.create().setUploadsDirectory(SysConfig.resourcesPath()+SysConfig.getConf("fileOptions.dir")).setDeleteUploadedFilesOnEnd((Boolean) SysConfig.getConf("fileOptions.deleteUploadedFilesOnEnd"));
        //扫描路由
        RouteUtils routeUtils = new RouteUtils(vertx);
        routeUtils.enableCORS();
        routeUtils.enableSockJSCORS();
        routeUtils.scanRoute("makjust.route");
        routeUtils.createLocalSession();
        if ((Boolean) SysConfig.getConf("enWeb")) routeUtils.setStaticRoute(SysConfig.getStaticPath(),"(?!/(api|ws))/.*");
        routeUtils.setVueRouteEnable("(?!/(api|ws))/.*");
        routeUtils.mountAPIRoute("/api/*",bodyHandler);
        routeUtils.mountWSRoute("/ws/*");
        routeUtils.startHttpServer(8080);


    }
}
