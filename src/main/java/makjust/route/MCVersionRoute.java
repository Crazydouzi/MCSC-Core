package makjust.route;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.*;
import makjust.entity.MCServer;
import makjust.service.MCServerService;
import makjust.service.MCVersionService;
import makjust.service.impl.MCServerServiceImpl;
import makjust.service.impl.MCVersionServiceImpl;

@RoutePath("/version")
public class MCVersionRoute extends AbstractRoute {
    MCVersionService versionService = new MCVersionServiceImpl();
    MCServerService mcServerService = new MCServerServiceImpl();

    // 获取已上传版本列表（可部署版本）
    @Request(value = "/list", method = HttpMethod.GET)
    public RoutingContext getVersionList(RoutingContext ctx) {
        versionService.getVersionList(ar -> ctx.json(returnJson(200, ar.result())));
        return ctx;
    }

    @Request(value = "/enableServerInfo", method = HttpMethod.GET)
    public RoutingContext getEnableServer(RoutingContext ctx) {

        mcServerService.getEnableServer(ar -> ctx.json(returnJson(200, ar.result())));
        return ctx;
    }

    @Request(value = "/changeVersion", method = HttpMethod.POST)
    public RoutingContext changeVersion(RoutingContext ctx, @JsonData MCServer server) {
        versionService.changeEnableVersion(server, ar -> ctx.json(returnJson(200, ar.result())));
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
    @Request(value = "/MCServerUpload", method = HttpMethod.POST)
    public RoutingContext MCServerUpload(RoutingContext ctx, @RequestParam("object") JsonObject object, @RequestParam("file") FileUpload file, @RequestParam("file2") FileUpload file2) {
        ctx.json(new JsonObject().put("K", "OK"));
        return ctx;
    }

    @Request(value = "/scanVersion", method = HttpMethod.POST)
    //扫描版本
    public RoutingContext scanVersion(RoutingContext ctx) {
        versionService.serverScanner(vertx, ar -> ctx.json(returnJson(200, ar.result())));
        return ctx;
    }

}
