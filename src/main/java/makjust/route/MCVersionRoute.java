package makjust.route;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import makjust.annotation.*;
import makjust.dto.RemoteVersionInfoDTO;
import makjust.pojo.MCServer;
import makjust.pojo.MCSetting;
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
    @Request(value = "/mcServerUpload", method = HttpMethod.POST)
    public RoutingContext mcServerUpload(RoutingContext ctx, @RequestParam("MCServer") MCServer mcServer, @RequestParam("MCSetting") MCSetting mcSetting, @RequestParam("file") FileUpload file) {

        ctx.json(new JsonObject().put("K", file.fileName()));
        return ctx;
    }

    @Request(value = "/installRemoteMCServer", method = HttpMethod.POST)
    public RoutingContext installRemoteMCServer(RoutingContext ctx, @JsonData("MCServer") MCServer mcServer, @JsonData("MCSetting") MCSetting mcSetting, @JsonData("versionInfo") RemoteVersionInfoDTO versionInfo) {
        versionService.installMCServerFromRemote(vertx, mcServer, mcSetting, versionInfo, ar -> {
            if (ar.succeeded()) {
                ctx.json(returnJson(ar.result()));
            } else {
                ctx.json(returnJson(500,ar.cause().getMessage()));
            }
        });
        return ctx;
    }
    @Request(value = "uninstallMCServer",method = HttpMethod.DELETE)
    public  RoutingContext uninstallMCServer(RoutingContext ctx,@RequestParam MCServer server){
        System.out.println(server);
        ctx.json(null);
        return ctx;

    }    @Request(value = "/scanVersion", method = HttpMethod.POST)
    //扫描版本
    public RoutingContext scanVersion(RoutingContext ctx) {
        versionService.serverScanner(vertx, ar -> ctx.json(returnJson(200, ar.result())));
        return ctx;
    }

}
