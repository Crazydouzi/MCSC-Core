package makjust.utils;

import io.vertx.core.http.ServerWebSocket;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerMsgThread extends Thread {
    private ServerWebSocket webSocket;
    private BufferedReader msg;

    public ServerMsgThread(BufferedReader msg, ServerWebSocket webSocket) {
        this.msg = msg;
        this.webSocket = webSocket;
    }

    public void run() {
        String line = "";
        try {
            while ((line = msg.readLine()) != null) {
                webSocket.writeTextMessage(line);
                System.out.println("Minecraft控制台输出："+line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
