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
        // Create a router endpoint for the static content.
        router.route().handler(StaticHandler.create().setWebRoot(getConfig.getStaticPath()));
        router.route("/static/*").handler(StaticHandler.create());
        // Allow events for the designated addresses in/out of the event bus bridge
        SockJSHandlerOptions options = new SockJSHandlerOptions();
        // Create the event bus bridge and add it to the router.
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);
        router.route("/CMD/*").subRouter(sockJSHandler.socketHandler(sockJSSocket -> {
            sockJSSocket.handler(sockJSSocket::write);
            sockJSSocket.handler(ws -> {
                try {
//                   打印接受到的数据
                    System.out.println("接收:" + ws.toString());
                    vertx.eventBus().publish("cmdReq", ws.toString());
                    //向客户端发送数据
                    vertx.eventBus().consumer("cmdRes", r -> {
                        sockJSSocket.write((String) r.body());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }));
        // Start the web server and tell it to use the router to handle requests.
        vertx.createHttpServer().requestHandler(router).listen(8080);


    }
}
