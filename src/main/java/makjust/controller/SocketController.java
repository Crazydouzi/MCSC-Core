package makjust.controller;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import makjust.annotation.Controller;
import makjust.annotation.Socket;
@Deprecated
@Controller("/CMD")
public class SocketController {
    @Socket("/process")
    public Router processSocket(Vertx vertx, SockJSHandler sockJSHandler){
        return sockJSHandler.socketHandler(sockJSSocket -> {
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
        });
    }
}
