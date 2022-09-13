package makjust.utils;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.ServerWebSocket;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerMsgThread extends Thread {
    private BufferedReader msg;
    private Vertx vertx;

    public ServerMsgThread(BufferedReader msg,Vertx vertx) {
        this.msg = msg;
        this.vertx=vertx;
    }

    public void run() {
        String line = "";
        try {
            while ((line = msg.readLine()) != null) {
//              推送消息
                vertx.eventBus().publish("psMsg",line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
