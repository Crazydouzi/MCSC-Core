package makjust.serverCore;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import makjust.utils.EnvOptions;
import makjust.utils.SysConfig;

import java.io.*;

public class ProcessServer {
    private final ProcessBuilder builder;
    private Process process;
    private final Vertx vertx;
    private BufferedReader reader;
    private Thread msgThead;
    private MessageConsumer<String> msgConsumer;

    public ProcessServer(File path, String command, Vertx vertx) {
        this.vertx = vertx;
        builder = new ProcessBuilder("cmd.exe", "/c", command);
        builder.directory(path);
        builder.redirectErrorStream(true);
    }

    public void start() throws IOException {
        process = builder.start();
        reader = new BufferedReader(new InputStreamReader(getInputStream(),
                (String) SysConfig.getConf("charset")));
        OutputStream os = process.getOutputStream();
        //获取Socket接收到的指令
        msgConsumer = vertx.eventBus().consumer("processServer.cmdReq", data -> {
            System.out.println(os);
            if (process!=null){
                try {
                    os.write((data.body() + "\n").getBytes());
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("控制台输出：MC服务器提前关闭！请查看log文件");
                vertx.eventBus().publish("processServer.cmdRes", "控制台输出：MC服务器提前关闭！请查看log文件");
                stop();
            }

        });
//      新开一条线程避免阻塞，用于推送消息
        msgThead = new Thread(() -> {
            String line;
            try {
                while ((line = reader.readLine()) != null) {
//              向Socket推送消息
                    vertx.eventBus().publish("processServer.cmdRes", line);
                    if (line.contains("Saving chunks for level")){
                        System.out.println("控制台输出：MC服务器提前关闭！请查看log文件");
                        vertx.eventBus().publish("processServer.cmdRes", "控制台输出：MC服务器提前关闭！请查看log文件");
                        stop();
                        break;
                    }
                    System.out.println("控制台输出：" + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        msgThead.start();
    }


    public void stop() {
        try {
            getOutputStream().write("stop \n".getBytes());
            getOutputStream().flush();
            getOutputStream().write("exit \n".getBytes());
            getOutputStream().flush();
            process.waitFor();
            process.destroy();
            process.destroyForcibly();
        } catch (Exception e) {
            e.printStackTrace();
            msgConsumer.unregister();
            vertx.eventBus().unregisterCodec("processServer.cmdRes");
        }finally {
            if (msgConsumer != null) {
                msgConsumer.unregister();
                vertx.eventBus().unregisterCodec("processServer.cmdRes");
            }
            msgThead.interrupt();
            process = null;
            System.gc();
            EnvOptions.setServerStatus(false);
        }

    }


    public OutputStream getOutputStream() {
        return process.getOutputStream();
    }

    public InputStream getInputStream() {
        return process.getInputStream();
    }
}
