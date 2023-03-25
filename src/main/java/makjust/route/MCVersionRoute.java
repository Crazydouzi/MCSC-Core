package makjust.route;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.HttpMethod;
import makjust.annotation.Request;
import makjust.annotation.RequestBody;
import makjust.annotation.RoutePath;
import makjust.entity.MCServer;
import makjust.service.MCServerService;
import makjust.service.MCVersionService;
import makjust.service.impl.MCServerServiceImpl;
import makjust.service.impl.MCVersionServiceImpl;

@RoutePath("/version")
public class MCVersionRoute extends AbstractRoute {
    MCVersionService versionService = new MCVersionServiceImpl();
    MCServerService mcServerService=new MCServerServiceImpl();

    // 获取已上传版本列表（可部署版本）
    @Request(value = "/list", method = HttpMethod.POST)
    public RoutingContext getVersionList(RoutingContext ctx) {
        versionService.getVersionList(ar -> ctx.json(returnJson(200, ar.result())));
        return ctx;
    }
    @Request(value = "/enableServerInfo",method = HttpMethod.POST)
    public RoutingContext getEnableServer(RoutingContext ctx){

        mcServerService.getEnableServer(ar->{
            ctx.json(returnJson(200,ar.result()));
        });
        return ctx;
    }
    @Request(value = "/changeVersion", method = HttpMethod.POST)
    public RoutingContext changeVersion(RoutingContext ctx,@RequestBody MCServer server){
        versionService.changeEnableVersion(server,ar -> ctx.json(returnJson(200, ar.result())));
        return ctx;
    }

    //获取远程仓库可用版本
    public JsonObject getStoreVersionList() {
        return null;
    }

    //配置远程仓库
    public JsonObject setStore() {
        return null;
    }


    //文件上传
    public JsonObject fileUpload() {
        return null;
    }

    @Request(value = "/scanVersion", method = HttpMethod.POST)
    //扫描版本
    public RoutingContext scanVersion(RoutingContext ctx) {
        versionService.serverScanner(vertx, ar -> ctx.json(returnJson(200, ar.result())));
        return ctx;
    }

}
