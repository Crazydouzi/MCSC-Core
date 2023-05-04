package makjust.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.CookieSameSite;
import io.vertx.ext.web.handler.BodyHandler;
import makjust.annotation.Deploy;
import makjust.auth.UserAuth;
import makjust.route.AbstractRoute;
import makjust.utils.RouteUtils;
import makjust.utils.SysConfig;

@Deploy
public class MainVerticle extends AbstractVerticle {
    @Override
    public void start() {
        System.out.println("主VERTICLE挂载中。。。。。");
        AbstractRoute.vertx = vertx;
        BodyHandler bodyHandler = BodyHandler.create().setUploadsDirectory(SysConfig.resourcesPath() + SysConfig.getConf("fileOptions.dir")).setDeleteUploadedFilesOnEnd((Boolean) SysConfig.getConf("fileOptions.deleteUploadedFilesOnEnd"));
        //扫描路由
        RouteUtils routeUtils = new RouteUtils(vertx);
        //SameSite设定
        routeUtils.createLocalSession(CookieSameSite.valueOf((String) SysConfig.getConf("CookieSameSite")));
        //跨域设定
        if ((Boolean) SysConfig.getConf("CORS")){routeUtils.enableCORS();}
        routeUtils.scanRoute("makjust.route");
        UserAuth auth=new UserAuth();
        routeUtils.route().handler(ctx -> auth.auth(ctx,"/api/user/userLogin","/api/user/getCode","/api/user/forget"));
        if ((Boolean) SysConfig.getConf("enWeb"))
            routeUtils.setStaticRoute(SysConfig.getStaticPath(), "(?!/(api|ws))/.*");
        routeUtils.setVueRouteEnable("(?!/(api|ws))/.*");
        routeUtils.mountAPIRoute("/api/*", bodyHandler);
        routeUtils.mountWSRoute("/ws/*");
        routeUtils.startHttpServer(8080);


    }
}
