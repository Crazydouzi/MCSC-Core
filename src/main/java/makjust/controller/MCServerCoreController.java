package makjust.controller;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import makjust.annotation.*;
import makjust.entity.MCServer;
import makjust.entity.MCSetting;
import makjust.serverCore.ProcessServer;
import makjust.utils.getConfig;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Controller("/server")
public class MCServerCoreController {
    ProcessServer mcServer;
    // 修改服务器选项
    public Json editSetting(@RequestBody List<MCSetting> settingList){
        return new Json();
    }
    // 根据服务器id查询全部设置
    public Json getSetting(@RequestBody MCServer server){
        return new Json();
    }
    // 修改MC核心启动参数
    public Json coreParamSetting(@RequestBody MCServer mcServer){
        return new Json();
    }
    @Request(value = "/start",method = HttpMethod.POST)
    public JsonObject serverStart(Vertx vertx) throws URISyntaxException, IOException {
        String DIR = getConfig.getCorePath("/194");
        String CMD = getConfig.object.getJsonObject("mcServer").getString("def_cmd");
        mcServer = new ProcessServer(new File(DIR), CMD,vertx);
        mcServer.start();
        return new JsonObject();
    }
    @Request(value = "/stop",method = HttpMethod.POST)
    public JsonObject serverStop(Vertx vertx) throws IOException {
        mcServer.stop();
        return new JsonObject();
    }
    @Socket("/process")
    public Router processSocket(Vertx vertx, SockJSHandler sockJSHandler){
        return sockJSHandler.socketHandler(sockJSSocket -> {
            // 向客户端发送数据
            vertx.eventBus().consumer("processServer.cmdRes", r -> {
                sockJSSocket.write((String) r.body());
            });
            sockJSSocket.handler(ws -> {
                try {
                    // 推送接收到的到的数据
                    vertx.eventBus().publish("processServer.cmdReq", ws.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
