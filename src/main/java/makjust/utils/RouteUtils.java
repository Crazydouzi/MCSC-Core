package makjust.utils;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.SessionStore;
import makjust.annotation.RoutePath;

import java.util.HashSet;
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

    public void enableCORS() {
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("Access-Control-Request-Headers");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("X-PINGARUNER");

        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        /*
         * these methods aren't necessary for this sample,
         * but you may need them for your projects
         */
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.PUT);
        allowedMethods.add(HttpMethod.HEAD);
        router.route().handler(CorsHandler.create()
                .allowedHeaders(allowedHeaders)
                .allowedMethods(allowedMethods)
        );
    }

    public void enableSockJSCORS() {
        Set<String> exposedHeaders = new HashSet<>();
        exposedHeaders.add("Access-Control-Allow-Headers");
        exposedHeaders.add("Access-Control-Allow-Method");
        exposedHeaders.add("Access-Control-Max-Age");
        exposedHeaders.add("Access-Control-Request-Headers");
        exposedHeaders.add("X-Frame-Options");
        wsRouter.route().handler(CorsHandler.create().allowedHeader("Content-Type").exposedHeaders(exposedHeaders).allowCredentials(true));
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

    public void createLocalSession() {
        SessionStore store = SessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(store);
        router.route().handler(sessionHandler);
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
    public Route route(){return router.route();}

}
