package makjust.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.CookieSameSite;
import io.vertx.ext.web.handler.BodyHandler;
import makjust.annotation.Deploy;
import makjust.config.auth.UserAuth;
import makjust.config.cors.DefaultCORS;
import makjust.route.AbstractRoute;
import makjust.utils.RouteUtils;
import makjust.utils.SysConfig;

@Deploy
public class MainVerticle extends AbstractVerticle {
    @Override
    public void start() {
        try {
            System.out.println("主VERTICLE挂载中。。。。。");
            AbstractRoute.vertx = vertx;
            BodyHandler bodyHandler = BodyHandler.create().setUploadsDirectory(SysConfig.resourcesPath() + SysConfig.getConf("fileOptions.dir")).setDeleteUploadedFilesOnEnd((Boolean) SysConfig.getConf("fileOptions.deleteUploadedFilesOnEnd"));
            RouteUtils routeUtils = new RouteUtils(vertx);
            //扫描路由
            routeUtils.scanRoute("makjust.route");
            //跨域设定
            if ((Boolean) SysConfig.getConf("CORS")){routeUtils.enableCORS(new DefaultCORS().CORS());}
            //初始化Session与SameSite设定
            routeUtils.createLocalSession(CookieSameSite.valueOf((String) SysConfig.getConf("CookieSameSite")));
            // 设置静态页面
            if ((Boolean) SysConfig.getConf("enWeb")){routeUtils.setStaticRoute(SysConfig.getStaticPath(), "(?!/(api|ws))/.*");}
            //支持单页路由
            routeUtils.setVueRouteEnable("(?!/(api|ws))/.*");
            //权限认证
            UserAuth auth=new UserAuth();
            routeUtils.route().handler(ctx -> auth.auth(ctx,"/api/user/userLogin","/api/user/getCode","/api/user/forget"));
            //挂载路由
            routeUtils.mountAPIRoute("/api/*", bodyHandler);
            routeUtils.mountWSRoute("/ws/*");
            routeUtils.startHttpServer(8080);
        }catch (Exception e){e.printStackTrace();}



    }
}
