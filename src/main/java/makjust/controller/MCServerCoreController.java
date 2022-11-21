package makjust.controller;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import makjust.annotation.Controller;
import makjust.annotation.RequestBody;
import makjust.annotation.Socket;
import makjust.entity.MCServer;
import makjust.entity.MCSetting;

import java.util.List;

@Controller("/server")
public class MCServerCoreController {
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
    @Socket("/process")
    public Router processSocket(Vertx vertx, SockJSHandler sockJSHandler){
        return sockJSHandler.socketHandler(sockJSSocket -> {
            // 向客户端发送数据
            vertx.eventBus().consumer("cmdRes", r -> {
                sockJSSocket.write((String) r.body());
            });
            sockJSSocket.handler(ws -> {
                try {
                    // 推送接收到的到的数据
                    vertx.eventBus().publish("cmdReq", ws.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
