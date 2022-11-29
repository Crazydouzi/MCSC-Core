package makjust.verticle;

import io.vertx.core.AbstractVerticle;
import makjust.serverCore.ProcessServer;
import makjust.utils.sysConfig;

import java.io.*;
import java.net.URISyntaxException;
@Deprecated
//@Deploy(worker = true)
public class CMDWorkVerticle extends AbstractVerticle {
     String DIR = sysConfig.getCorePath("/194");
     String CMD = sysConfig.object.getJsonObject("mcServer").getString("def_cmd");
    //启动服务器时候开启MCServer Process
    ProcessServer mcServer = new ProcessServer(new File(DIR), CMD,vertx);

    public CMDWorkVerticle() throws URISyntaxException {
    }

    @Override
    public void start() throws Exception {
        mcServer.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(mcServer.getInputStream(),
                sysConfig.object.getJsonObject("core").getString("cmd_charset")));
        OutputStream os = mcServer.getOutputStream();
        //获取接收到的指令
        vertx.eventBus().consumer("processServer.cmdReq", data -> {
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
                    vertx.eventBus().publish("processServer.cmdRes", line);
                    System.out.println("控制台输出："+line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("关闭");
        mcServer.stop();
        super.stop();
    }


}
