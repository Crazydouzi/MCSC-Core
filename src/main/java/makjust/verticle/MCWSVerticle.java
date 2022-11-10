package makjust.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
@Deprecated
public class MCWSVerticle extends AbstractVerticle {

    @Override
    public void start()  {
        //      创建一个webSocket服务
        HttpServer server = vertx.createHttpServer();
        server.webSocketHandler(webSocket -> {
//        打印输出
            webSocket.handler(socket -> {
                try {
//                   打印接受到的数据
                    System.out.println("接收:" + socket.toString());
                    vertx.eventBus().publish("cmdReq", socket.toString());
                    //                  向客户端发送数据
                    vertx.eventBus().consumer("cmdRes", r -> {
                        webSocket.writeTextMessage((String) r.body());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
//           当断开连接时
            webSocket.closeHandler(close -> {
                System.out.println("Client 断开链接");
            });
        }).listen(23333, "127.0.0.1", res -> {
            if (res.succeeded()) {
                System.out.println("Socket服务器启动成功");
            } else {
                System.out.println("Socket服务器启动失败");
            }
        });
    }
}