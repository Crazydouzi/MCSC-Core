package makjust.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import makjust.serverCore.MCServer;
import makjust.utils.ServerMsgThread;

import java.io.*;

public class VertexServer extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new VertexServer());
    }

    @Override
    public void start() throws IOException, InterruptedException {
        final String DIR = "I:\\Documents\\mcs";
        final String CMD = "cmd";
        //启动服务器时候开启MCServer Process
        MCServer mcServer = new MCServer(new File(DIR), CMD);
        mcServer.start();
        OutputStream os = mcServer.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(mcServer.getInputStream(), "GBK"));

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

//        打印输出
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //                  向客户端发送数据
                try {
                    //开启一条线程用来向客户端发送消息 避免阻塞
                    new ServerMsgThread(reader,webSocket).start();
                }catch (Exception e){
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