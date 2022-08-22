package mcss;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class WSServer extends WebSocketServer {
    private MCServer server;
    private OutputStream os;
    public WSServer (){
    }
    public WSServer(int port, MCServer server) {
        super(new InetSocketAddress(port));
        this.server = server;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        webSocket.send("Connected!\n");
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        try {
//            接收到客户机指令s 写入到server输出流
            os.write((s+"\n").getBytes());
            System.out.println("收到信息"+s);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
        this.os = server.getOutputStream();
    }
}
