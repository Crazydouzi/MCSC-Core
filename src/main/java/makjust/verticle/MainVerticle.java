package makjust.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import makjust.utils.getConfig;

public class MainVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        // 静态资源路由
        if (getConfig.getCoreConf().getBoolean("enWeb")) {
            router.route().handler(StaticHandler.create().setWebRoot(getConfig.getStaticPath()));
            router.route("/static/*").handler(StaticHandler.create());
        }
//      SockJS服务
        SockJSHandlerOptions options = new SockJSHandlerOptions();
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);
        router.route("/CMD/*").subRouter(sockJSHandler.socketHandler(sockJSSocket -> {
            //向客户端发送数据
            vertx.eventBus().consumer("cmdRes", r -> {
                System.out.println("消息推送输出：" + r.body());
                sockJSSocket.write((String) r.body());
            });
            sockJSSocket.handler(ws -> {
                try {
//                   打印接受到的数据
                    vertx.eventBus().publish("cmdReq", ws.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }));
        // REST API
        // 登录验证
        router.post("/account/login").handler(ctx -> {
//            ctx.json(new AuthController().userLogin(ctx));
        });
        router.post("/account/register").handler(ctx -> {
            System.out.println(ctx.getBodyAsJson());

//            ctx.json(new AuthController().userRegister(ctx));
        });
        // 服务器状态信息
        // 插件子路由注册
        //
        vertx.createHttpServer().requestHandler(router).listen(8080);


    }
}
