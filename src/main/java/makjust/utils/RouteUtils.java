package makjust.utils;

import io.vertx.core.Vertx;
import io.vertx.core.http.CookieSameSite;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import makjust.annotation.RoutePath;

import java.util.Set;

public class RouteUtils {
    private final Vertx vertx;
    private final Router apiRouter;
    private final Router wsRouter;
    private final Router router;
    private final HttpServer server;

    public RouteUtils(Vertx vertx) {
        this.vertx = vertx;
        //主路由
        this.router = Router.router(vertx);
        //api子路由
        this.apiRouter = Router.router(vertx);
        // ws子路由(SockJs)
        this.wsRouter = Router.router(vertx);
        this.server = vertx.createHttpServer();

    }

    public void scanRoute(String scanPath) {
        try {
            Set<Class<?>> classes = ClassScanUtil.scanByAnnotation(scanPath, RoutePath.class);
            RouteScanner routeScanner = new RouteScanner(vertx);
            for (Class<?> cls : classes) {
                Object controller = cls.getConstructor().newInstance();
                routeScanner.routerMapping(controller, apiRouter, wsRouter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 默认方法 指定文件夹
     */
    public void setStaticRoute(String webRootLoc) {
        router.route().method(HttpMethod.GET)
                .handler(
                        StaticHandler
                                .create(webRootLoc)
                                .setCachingEnabled(false)
                                .setDefaultContentEncoding("utf-8"));
    }

    public void setStaticRoute(String webRootLOC, String excludePathRegex) {
        router.errorHandler(404, ctx -> {
            if (ctx.request().method() == HttpMethod.GET && ctx.request().uri().matches(excludePathRegex)) {
                ctx.reroute("/index.html");
            } else {
                ctx.json(new JsonObject().put("code", 404).put("msg", "页面走丢了哟~"));
            }
        });
        router.route().pathRegex(excludePathRegex).method(HttpMethod.GET)
                .handler(StaticHandler.create(webRootLOC).setCachingEnabled(false).setDefaultContentEncoding("utf-8"));
    }

    public void setVueRouteEnable(String apiRouteRegex) {
        router.errorHandler(404, ctx -> {
            if (ctx.request().method() == HttpMethod.GET && ctx.request().uri().matches(apiRouteRegex)) {
                ctx.reroute("/index.html");
            } else {
                ctx.response().setStatusCode(404);
                ctx.json(new JsonObject().put("code", 404).put("msg", "页面走丢了哟~"));
            }
        });
    }

    /**
     * 挂载子路由到主路由
     * 例如："/ws/*"”
     *
     * @param apiPathPrefix apiURL前缀
     * @param wsPathPrefix  wsURL前缀
     */
    public void mountAllRoute(String apiPathPrefix, String wsPathPrefix) {
        mountWSRoute(wsPathPrefix);
        mountAPIRoute(apiPathPrefix);
    }

    public void enableCORS(CorsHandler handler) {
        router.route().handler(handler
        );
    }

    public void createLocalSession(CookieSameSite cookieSameSite) {
        SessionStore store = LocalSessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(store);
        sessionHandler
                .setSessionCookieName("SSID")
                .setCookieSameSite(cookieSameSite)
                .setCookieSecureFlag(true);
        router.route().handler(sessionHandler);
    }

    public void createLocalSession() {
        SessionStore store = SessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(store);
        router.route().handler(sessionHandler);
    }

    public void mountAPIRoute(String URLPrefix) {
        router.route(URLPrefix)
                .consumes("*/json")
                .consumes("multipart/form-data")
                .consumes("application/x-www-form-urlencoded")
                .handler(BodyHandler.create()).subRouter(apiRouter);
    }

    public void mountAPIRoute(String URLPrefix, BodyHandler bodyHandler) {
        router.route(URLPrefix)
                .consumes("*/json")
                .consumes("multipart/form-data")
                .consumes("application/x-www-form-urlencoded")
                .handler(bodyHandler).subRouter(apiRouter);
    }

    public void mountWSRoute(String URLPrefix) {
        router.route(URLPrefix).subRouter(wsRouter);
    }


    public void startHttpServer(int port) {
        server.requestHandler(router()).listen(port);

    }

    // this is a default port ,it will use port 8080;
    public void startHttpServer() {
        startHttpServer(8080);
    }

    //装载到子路由


    public Router router() {
        return router;
    }

    public Route route() {
        return router.route();
    }

}
