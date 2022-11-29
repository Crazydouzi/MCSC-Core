package makjust.serverCore;

import io.vertx.core.Vertx;
import makjust.utils.sysConfig;

import java.io.*;

public class ProcessServer {
    private ProcessBuilder builder;
    private Process process;
    private Vertx vertx;
    public ProcessServer(){}
    public ProcessServer(File path, String command,Vertx vertx){
        this.vertx=vertx;
        builder=new ProcessBuilder("cmd","/c",command);
        builder.directory(path);
        builder.redirectErrorStream(true);
    }
    public void start() throws IOException {
        process=builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(),
                sysConfig.object.getJsonObject("core").getString("cmd_charset")));
        OutputStream os = getOutputStream();
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
    public void stop() throws IOException {
        System.out.println("process关闭");
        process.destroy();
    }
    public OutputStream getOutputStream(){
        return process.getOutputStream();
    }
    public InputStream getInputStream(){
        return process.getInputStream();
    }
}
