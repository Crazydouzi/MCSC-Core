package makjust.route;

import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import makjust.annotation.*;
import makjust.entity.MCServer;
import makjust.entity.MCSetting;
import makjust.service.MCServerService;
import makjust.service.impl.MCServerServiceImpl;
import makjust.utils.EnvOptions;

import java.util.HashMap;
import java.util.Map;

@RoutePath("/server")
public class MCServerRoute extends AbstractRoute {
    private final MCServerService serverService = new MCServerServiceImpl();

    // 修改服务器选项
    @Request(value = "/modifyServerOption", method = HttpMethod.POST)
    public RoutingContext editServerOption(@RequestBody MCSetting setting) {
        serverService.setServerSetting(setting, ar -> ctx.json(returnJson(200, ar.result())));
        return ctx;
    }

    // 修改MC服务器信息
    @Request(value = "/modifyServerInfo", method = HttpMethod.POST)
    public RoutingContext editCoreSetting(@RequestBody MCServer mcServer) {
        serverService.setCoreSetting(mcServer, ar -> ctx.json(returnJson(ar.result())));
        return ctx;
    }

    // 根据服务器id查询全部设置
    @Request(value = "/getSettingList", method = HttpMethod.POST)
    public RoutingContext getSetting(@RequestBody MCSetting setting) {
        serverService.getSetting(vertx, setting, ar -> {
            ctx.response().setStatusCode(200);
            ctx.json(returnJson(200, ar.result()));
        });
        return ctx;
    }


    // 开启MC服务器
    @Request(value = "/start", method = HttpMethod.POST)
    public RoutingContext serverStart() {
        serverService.serverStart(vertx, ar -> {
            ctx.response().setStatusCode(200);
            ctx.json(returnJson(ar.result()));
        });
        return ctx;
    }

    // 关闭MC服务器
    @Request(value = "/stop", method = HttpMethod.POST, async = false)
    public JsonObject serverStop() {
        JsonObject jsonObject = new JsonObject();
        boolean flag = serverService.serverStop(vertx);
        if (flag) {
            return jsonObject.put("msg", "服务器关闭成功").put("data", true);
        } else return jsonObject.put("msg", "关闭失败").put("data", false);
    }

    @Request(value = "/status", method = HttpMethod.POST)
    public RoutingContext getServerStatus() {
        ctx.json(returnJson(200, new JsonObject().put("data", serverService.serverStatus())));
        return ctx;
    }

    @SockJSSocket("/cmd")
    public Router processSocket(SockJSHandler sockJSHandler) {
        return sockJSHandler.socketHandler(sockJSSocket -> {
            // 向客户端(Web)发送数据
            vertx.eventBus().consumer("processServer.cmdRes", r -> {
                if (EnvOptions.getServerStatus()) {
                    sockJSSocket.write((String) r.body());
                }
            });
            //接收Client发送的消息
            sockJSSocket.handler(ws -> {
                try {
                    if (EnvOptions.getServerStatus()) {
                        if (ws.toString().equalsIgnoreCase("STOP")) {
                            this.serverStop();
                        } else {
                            // 推送接收到的到的数据
                            vertx.eventBus().send("processServer.cmdReq", ws.toString());
                            System.out.println("客户端CMD:" + ws);
                        }
                    } else {
                        sockJSSocket.write("服务器已关闭。。。");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }


}
