package mcss;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MCServer {
    private ProcessBuilder builder;
    private Process process;
    MCServer(File path, String command){
        builder=new ProcessBuilder("cmd","/c",command);
        builder.directory(path);
        builder.redirectErrorStream(true);
    }
    void start() throws IOException {
        process=builder.start();
    }
    void stop() throws IOException {
        process.destroy();
    }
    OutputStream getOutputStream(){
        return process.getOutputStream();
    }
    InputStream getInputStream(){
        return process.getInputStream();
    }
}
