package makjust.route;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import makjust.annotation.*;
import makjust.dto.SystemConfigDTO;
import makjust.service.SystemService;
import makjust.service.impl.SystemServiceImpl;

import java.net.UnknownHostException;

@RoutePath("/system")
public class SystemInfoRoute extends AbstractRoute {
    private final SystemService systemService = new SystemServiceImpl();

    // 使用率报告
    @Request(value = "/getInfo", method = HttpMethod.GET)
    public RoutingContext usageInfo(RoutingContext ctx) throws UnknownHostException {
        ctx.json(returnJson(200, systemService.getSystemInfo()));
        return ctx;

    }

    //获取CPU实时占用率
    @Request(value = "/getCpuUsage", method = HttpMethod.GET)
    public RoutingContext cpuUsage(RoutingContext ctx) {
        ctx.json(returnJson(200, systemService.getCpuUsage()));
        return ctx;
    }

    //获取内存使用率
    @Request(value = "/getMemUsage", method = HttpMethod.GET)
    public RoutingContext memoryUsage(RoutingContext ctx) {
        ctx.json(returnJson(200, systemService.getMemoryUsage()));
        return ctx;
    }
    //获取系统设置
    @Request(value = "/getSystemConfig", method = HttpMethod.GET)
    public RoutingContext getSystemConfig(RoutingContext ctx) {
        ctx.json(returnJson(200, systemService.getSystemConfig()));
        return ctx;
    }
    @Request(value = "/saveSystemConfig", method = HttpMethod.POST)
    public RoutingContext saveSystemConfig(RoutingContext ctx,@JsonData SystemConfigDTO config) {
        System.out.println("11");
        System.out.println(config);
        ctx.json(config);
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
