package makjust.route;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import makjust.annotation.HttpMethod;
import makjust.annotation.Request;
import makjust.annotation.RoutePath;
import makjust.annotation.SockJSSocket;
import makjust.service.SystemService;
import makjust.service.impl.SystemServiceImpl;

import java.net.UnknownHostException;

@RoutePath("/system")
public class SystemInfoRoute extends AbstractRoute {
    private final SystemService systemService = new SystemServiceImpl();

    // 使用率报告
    @Request(value = "/getInfo", method = HttpMethod.POST)
    public RoutingContext usageInfo() throws UnknownHostException {
        ctx.response().setStatusCode(200);
        ctx.json(returnJson(200, systemService.getSystemInfo()));
        return ctx;

    }

    //获取CPU实时占用率
    @Request(value = "/getCpuUsage", method = HttpMethod.POST)
    public RoutingContext cpuUsage() {
        ctx.response().setStatusCode(200);
        ctx.json(returnJson(200, systemService.getCpuUsage()));
        return ctx;
    }

    //获取内存使用率
    @Request(value = "/getMemUsage", method = HttpMethod.POST)
    public RoutingContext memoryUsage() {
        ctx.response().setStatusCode(200);
        ctx.json(returnJson(200, systemService.getMemoryUsage()));
        return ctx;
    }

    @SockJSSocket("/usage")
    public Router getUsage(SockJSHandler sockJSHandler) {
        return sockJSHandler.socketHandler(sockJSSocket -> {
            sockJSSocket.handler(ws->{
                if (ws.toString().equals("systemUsage")){
                    JsonObject object=new JsonObject();
                    object.put("cpuUsage",systemService.getCpuUsage());
                    object.put("memUsage",systemService.getMemoryUsage());
                    sockJSSocket.write(object.toString());
                }
            });

        });
    }


}
