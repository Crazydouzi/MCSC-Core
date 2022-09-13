package makjust.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import makjust.serverCore.MCServer;
import makjust.utils.ServerMsgThread;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class MCWSVerticle extends AbstractVerticle {

    @Override
    public void start() throws IOException, InterruptedException {
        final String DIR = "E:\\";
        final String CMD = "cmd";
        //启动服务器时候开启MCServer Process
        MCServer mcServer = new MCServer(new File(DIR), CMD);
        mcServer.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(mcServer.getInputStream(), "GBK"));
        new ServerMsgThread(reader,vertx).start();
        OutputStream os = mcServer.getOutputStream();

        //      创建一个webSocket服务
        HttpServer server = vertx.createHttpServer();
        server.webSocketHandler(webSocket -> {
//        打印输出
            webSocket.handler(socket -> {
                try {
//                   打印接受到的数据
                    System.out.println("接收:" + socket.toString());
                    os.write((socket.toString() + "\n").getBytes());
                    os.flush();

                    //                  向客户端发送数据
                    vertx.eventBus().consumer("psMsg",r->{
                        webSocket.writeTextMessage((String) r.body());
                        System.out.println("ws控制台输出："+ r.body());
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