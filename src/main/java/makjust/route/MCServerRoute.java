package makjust.route;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import makjust.annotation.*;
import makjust.entity.MCServer;
import makjust.service.MCServerService;
import makjust.service.impl.MCServerServiceImpl;
import makjust.utils.EnvOptions;

import java.util.Map;

@RoutePath("/server")
public class MCServerRoute extends AbstractRoute {
    private final MCServerService serverService = new MCServerServiceImpl();

    // 修改服务器选项
    @Request(value = "/modifyServerOption", method = HttpMethod.POST)
    public RoutingContext editServerOption(@RequestBody Map<String, Object> optionMap) {
        if (!(optionMap.containsKey("serverId") && optionMap.containsKey("settings"))) {
            ctx.response().setStatusCode(400);
            ctx.json(returnJson(400, "错误的请求参数"));
            return ctx;
        }
        serverService.setServerSetting(optionMap, ar -> ctx.json(returnJson(200, ar.result())));

        return ctx;
    }

    // 修改MC核心启动参数
    @Request(value = "/modifyServerInfo", method = HttpMethod.POST)
    public RoutingContext editCoreSetting(@RequestBody MCServer mcServer) {
        System.out.println(mcServer);
        ctx.json(returnJson(200, new JsonObject().put("data", mcServer)));
        return ctx;
    }

    // 根据服务器id查询全部设置
    @Request(value = "/getSettingList", method = HttpMethod.POST)
    public RoutingContext getSetting(@RequestBody MCServer server) {
        serverService.getSetting(vertx, server, ar -> {
            ctx.response().setStatusCode(200);
            ctx.json(returnJson(200, ar.result()));
        });
        return ctx;
    }


    // 开启MC服务器
    @Request(value = "/start", method = HttpMethod.POST)
    public RoutingContext serverStart() {
        serverService.serverStart(vertx, ar -> {
            if (ar.succeeded()) {
                ctx.response().setStatusCode(200);
                ctx.json(returnJson(20, "启动成功！"));
            } else {
                ctx.response().setStatusCode(200);
                ctx.json(returnJson(200, "启动失败！请重新扫描服务器"));

            }
        });
        return ctx;
    }

    // 关闭MC服务器
    @Request(value = "/stop", method = HttpMethod.POST, async = false)
    public JsonObject serverStop() {
        JsonObject jsonObject = new JsonObject();
        boolean flag = serverService.serverStop(vertx);
        if (flag) {
            vertx.eventBus().publish("processServer.cmdRes", "");
            return jsonObject.put("msg", "服务器关闭成功");
        } else return jsonObject.put("msg", "关闭失败");
    }

    @Socket("/process")
    public Router processSocket(SockJSHandler sockJSHandler) {
        return sockJSHandler.socketHandler(sockJSSocket -> {
            // 向客户端发送数据
            vertx.eventBus().consumer("processServer.cmdRes", r -> {
                if (EnvOptions.getServerStatus()) {
                    sockJSSocket.write((String) r.body());
                } else {
                    sockJSSocket.write("服务器已关闭。。。");

                }
            });
            //接收Client发送的消息
            sockJSSocket.handler(ws -> {
                try {
                    System.out.println("ws:" + ws.toString() + "processStatus:" + EnvOptions.getServerStatus());
                    if (EnvOptions.getServerStatus()) {
                        // 推送接收到的到的数据
                        vertx.eventBus().publish("processServer.cmdReq", ws.toString());
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
