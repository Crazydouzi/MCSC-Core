package makjust.route;

import io.vertx.core.json.JsonObject;
import makjust.annotation.RoutePath;
import makjust.annotation.HttpMethod;
import makjust.annotation.Request;
import makjust.service.SystemService;
import makjust.service.impl.SystemServiceImpl;

import java.net.UnknownHostException;

@RoutePath("/system")
public class SystemInfoRoute {
    private SystemService systemService= new SystemServiceImpl();
    // 使用率报告
    @Request(value = "/getInfo",method = HttpMethod.POST)
    public JsonObject usageInfo() throws UnknownHostException {
        return systemService.getSystemInfo();
    }
    //获取CPU实时占用率
    @Request(value = "/getCpuUsage",method = HttpMethod.POST)
    public JsonObject cpuUsage(){
        return systemService.getCpuUsage();
    }
    //获取内存使用率
    @Request(value = "/getMemUsage",method = HttpMethod.POST)
    public JsonObject memoryUsage(){return systemService.getMemoryUsage();}


}
