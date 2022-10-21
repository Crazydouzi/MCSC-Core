package makjust.verticle;

import com.oracle.webservices.internal.api.EnvelopeStyle;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import makjust.controller.AuthController;
import makjust.utils.getConfig;
public class MainVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        // 主路由
        Router router = Router.router(vertx);
        //api路由
        Router apiRouter = Router.router(vertx);
        // REST API
        // 登录验证
        apiRouter.post("/account/login").handler(ctx -> {
//            ctx.json(new AuthController().userLogin(ctx));
        });
        apiRouter.post("/account/register")
                .handler(ctx -> {
                    System.out.println(ctx.body().asJsonObject());
            ctx.json(new AuthController().userRegister(ctx.body().asJsonObject()));
                });
        // 服务器状态信息
        // 插件子路由注册
        //

        //      SockJS服务
        SockJSHandlerOptions options = new SockJSHandlerOptions();
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);
        router.route("/CMD/*").subRouter(sockJSHandler.socketHandler(sockJSSocket -> {
            // 向客户端发送数据
            vertx.eventBus().consumer("cmdRes", r -> {
                sockJSSocket.write((String) r.body());
            });
            sockJSSocket.handler(ws -> {
                try {
                    // 推送接收到的到的数据
                    vertx.eventBus().publish("cmdReq", ws.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }));
        // 静态资源路由
        if (getConfig.getCoreConf().getBoolean("enWeb")) {
            System.out.println(getConfig.getStaticPath());
            router.route().handler(StaticHandler.create().setWebRoot(getConfig.getStaticPath()));
        }
        //挂载子路由
        System.out.println(apiRouter.getRoutes());
        router.route("/api/*").consumes("*/json").handler(BodyHandler.create()).subRouter(apiRouter);
        vertx.createHttpServer().requestHandler(router).listen(8080);

    }
}
