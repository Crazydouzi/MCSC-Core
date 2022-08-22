package makjust.serverCore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MCServer {
    private ProcessBuilder builder;
    private Process process;
    public MCServer(File path, String command){
        builder=new ProcessBuilder("cmd","/c",command);
        builder.directory(path);
        builder.redirectErrorStream(true);
    }
    public void start() throws IOException {
        process=builder.start();
    }
    public void stop() throws IOException {
        process.destroy();
    }
    public OutputStream getOutputStream(){
        return process.getOutputStream();
    }
    public InputStream getInputStream(){
        return process.getInputStream();
    }
}
