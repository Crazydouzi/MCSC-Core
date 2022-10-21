package makjust.verticle;

import io.vertx.core.AbstractVerticle;
import makjust.serverCore.MCServer;
import makjust.utils.getConfig;

import java.io.*;

public class MCCWorkVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {

        final String DIR = getConfig.getCorePath("/194");
        final String CMD = getConfig.object.getJsonObject("mcServer").getString("def_cmd");
        //启动服务器时候开启MCServer Process
        MCServer mcServer = new MCServer(new File(DIR), CMD);
        mcServer.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(mcServer.getInputStream(),
                getConfig.object.getJsonObject("core").getString("charset")));
        OutputStream os = mcServer.getOutputStream();
        //获取接收到的指令退送到os
        vertx.eventBus().consumer("cmdReq", data -> {
            try {
                os.write((data.body() + "\n").getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
//      新开一条线程避免阻塞，用于推送消息
        new Thread(() -> {
            String line = "";
            try {
                while ((line = reader.readLine()) != null) {
//              推送消息
                    vertx.eventBus().publish("cmdRes", line);
                    System.out.println("控制台输出："+line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
