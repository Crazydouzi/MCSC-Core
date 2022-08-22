package mcss;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class  Main {
    private static final String DIR="I:\\Documents\\mcs";
    private static final String CMD="cmd";
    private static WSServer wsServer;
    public static void main(String[] args) throws IOException, InterruptedException {
        MCServer server=new MCServer(new File(DIR),CMD);
        server.start();
        wsServer = new WSServer(2333, server);

        wsServer.start();
       //获取Server process流
        BufferedReader reader=new BufferedReader(new InputStreamReader(server.getInputStream(), "GBK"));
//        打印输出
        String line;
        while ((line=reader.readLine())!=null){
            System.out.println("控制台输出："+line);
            wsServer.broadcast(line);
        }

        server.stop();
        wsServer.stop();
    }
    public WSServer getServer(){
        return wsServer;
    }
}
