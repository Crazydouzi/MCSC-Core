package makjust.route;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import makjust.annotation.*;
import makjust.entity.MCServer;
import makjust.entity.MCSetting;
import makjust.service.MCServerService;
import makjust.service.impl.MCServerServiceImpl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RoutePath("/server")
public class MCServerRoute extends AbstractRoute {
    private MCServerService serverService = new MCServerServiceImpl();
    
    // 修改服务器选项
    public Json editSetting(@RequestBody List<MCSetting> settingList) {
        return new Json();
    }

    // 根据服务器id查询全部设置
    public Json getSetting(@RequestBody MCServer server) {
        return new Json();
    }

    // 修改MC核心启动参数
    public Json coreParamSetting(@RequestBody MCServer mcServer) {
        return new Json();
    }

    // 开启MC服务器
    @Request(value = "/start", method = HttpMethod.POST)
    public JsonObject serverStart() throws URISyntaxException, IOException {
        JsonObject jsonObject = new JsonObject();
        boolean flag = serverService.serverStart(vertx);
        if (flag) return jsonObject.put("msg:", "启动成功");
        else return jsonObject.put("msg", "启动失败");
    }

    // 关闭MC服务器
    @Request(value = "/stop", method = HttpMethod.POST)
    public JsonObject serverStop() {
        JsonObject jsonObject = new JsonObject();
        boolean flag = serverService.serverStop(vertx);
        if (flag) return jsonObject.put("msg", "服务器关闭成功");
        else return jsonObject.put("msg", "关闭失败");
    }

    @Socket("/process")
    public io.vertx.ext.web.Router processSocket(SockJSHandler sockJSHandler) {
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
