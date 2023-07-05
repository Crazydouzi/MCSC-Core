package makjust.route;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import makjust.annotation.*;
import makjust.dto.MCServerDTO;
import makjust.bean.MCServer;
import makjust.bean.MCServerConfigFile;
import makjust.bean.MCSetting;
import makjust.service.MCServerService;
import makjust.service.impl.MCServerServiceImpl;
import makjust.utils.EnvOptions;


@RoutePath("/server")
public class MCServerRoute extends AbstractRoute {
    private final MCServerService serverService = new MCServerServiceImpl();

    // 修改服务器选项
    @Request(value = "/modifyServerOption", method = HttpMethod.POST)
    public RoutingContext editServerOption(RoutingContext ctx, @JsonData MCSetting setting) {
        serverService.setCoreSetting(setting, ar -> ctx.json(returnJson(ar.result())));
        return ctx;
    }

    // 修改MC服务器信息
    @Request(value = "/modifyServerInfo", method = HttpMethod.POST)
    public RoutingContext editCoreSetting(RoutingContext ctx, @JsonData MCServer mcServer) {
        serverService.setServerSetting(mcServer, ar -> ctx.json(returnJson(200, ar.result())));
        return ctx;

    }

    // 根据服务器id查询全部设置
    @Request(value = "/getSetting", method = HttpMethod.GET)
    public RoutingContext getSetting(RoutingContext ctx, @RequestParam MCSetting setting) {
        serverService.getSetting(vertx, setting, ar -> {
            ctx.response().setStatusCode(200);
            ctx.json(returnJson(200, ar.result()));
        });
        return ctx;
    }

    //获取全部配置文件
    @Request(value = "/getConfigList", method = HttpMethod.GET)
    public RoutingContext getConfigList(RoutingContext ctx, @RequestParam MCServer mcServer) {
        System.out.println(mcServer);
        serverService.getConfigFileList(vertx, mcServer, ar -> {
            if (ar.succeeded()) {
                ctx.json(returnJson(200, ar.result()));
            } else {
                ctx.json(returnJson(500, "服务器错误"));
            }
        });
        return ctx;
    }

    //读取配置文件
    @Request(value = "/readConfig", method = HttpMethod.GET)
    public RoutingContext readConfig(RoutingContext ctx, @RequestParam MCServer mcServer, @RequestParam MCServerConfigFile mcServerConfigFile) {
        serverService.readConfigFile(vertx, mcServer, mcServerConfigFile, ar -> {
            if (ar.succeeded()) {
                ctx.json(returnJson(200, ar.result()));
            } else {
                ctx.json(returnJson(500, "服务器错误"));
            }
        });
        return ctx;
    }

    @Request(value = "/getServerInfo", method = HttpMethod.GET)
    public RoutingContext getServerInfo(RoutingContext ctx,@RequestParam MCServer server) {
        serverService.getServerInfo(server,ar->{
            if (ar.succeeded()) {
                ctx.json(returnJson(200, ar.result().mapTo(MCServerDTO.class)));
            } else {
                ctx.json(returnJson(500, ar.cause().getMessage()));
            }
        });
        return ctx;
    }

    @Request(value = "/uploadPlugin", method = HttpMethod.POST)
    public RoutingContext uploadPlugin(RoutingContext ctx, @RequestParam("plugin") FileUpload fileUpload, @RequestParam("MCServer") MCServer server) {
        if (fileUpload == null) {
            ctx.json(returnJson(500, "未发现文件"));
        } else {
            serverService.uploadPlugins(vertx, server, fileUpload, ar -> {
                if (ar.succeeded()) {
                    ctx.json(returnJson(200, ar.result()));
                } else {
                    ctx.json(returnJson(500, ar.cause().getMessage()));
                }
            });
        }
        return ctx;
    }

    @Request(value = "/getPluginList", method = HttpMethod.GET)
    public RoutingContext getPluginList(RoutingContext ctx, @RequestParam MCServer mcServer) {
        serverService.getPluginList(vertx, mcServer, ar -> {
            if (ar.succeeded()) {
                ctx.json(returnJson(200, ar.result()));
            } else {
                ctx.json(returnJson(500, "服务器错误"));
            }
        });
        return ctx;
    }

    @Request(value = "/enablePlugin", method = HttpMethod.POST)
    public RoutingContext enablePlugin(RoutingContext ctx, @JsonData("MCServer") MCServer mcServer, @JsonData("plugin") String plugin) {
        System.out.println(mcServer);
        System.out.println(plugin);
        serverService.enablePlugins(vertx, mcServer, plugin, ar -> {
            if (ar.succeeded()) {
                ctx.json(returnJson(200, ar.result()));
            } else {
                ctx.json(returnJson(500, ar.cause().getMessage()));
            }
        });
        return ctx;

    }

    @Request(value = "/disablePlugin", method = HttpMethod.POST)
    public RoutingContext disablePlugin(RoutingContext ctx, @JsonData("MCServer") MCServer mcServer, @JsonData("plugin") String plugin) {
        serverService.disablePlugins(vertx, mcServer, plugin, ar -> {
            if (ar.succeeded()) {
                ctx.json(returnJson(200, ar.result()));
            } else {
                ctx.json(returnJson(500, (ar.cause().getCause())));
            }
        });
        return ctx;
    }

    @Request(value = "/deletePlugin", method = HttpMethod.DELETE)
    public RoutingContext deletePlugin(RoutingContext ctx, @RequestParam MCServer mcServer, @RequestParam String plugin) {
        serverService.deletePlugins(vertx, mcServer, plugin, ar -> {
            if (ar.succeeded()) {
                ctx.json(returnJson(200, ar.result()));
            } else {
                ctx.json(returnJson(500, (ar.cause().getCause())));
            }
        });
        return ctx;
    }

    // 开启MC服务器
    @Request(value = "/start", method = HttpMethod.POST)
    public RoutingContext serverStart(RoutingContext ctx) {
        serverService.serverStart(vertx, ar -> {
            ctx.response().setStatusCode(200);
            ctx.json(returnJson(ar.result()));
        });
        return ctx;
    }

    // 关闭MC服务器
    @Request(value = "/stop", method = HttpMethod.POST, async = false)
    public JsonObject serverStop() {
        boolean flag = serverService.serverStop(vertx);
        if (flag) {
            return returnJson(200,"服务器关闭成功");
        } else return returnJson(500,"服务器关闭失败");
    }

    @Request(value = "/status", method = HttpMethod.GET)
    public RoutingContext getServerStatus(RoutingContext ctx) {
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
                            sockJSSocket.close();
                        } else {
                            // 推送接收到的到的数据
                            vertx.eventBus().send("processServer.cmdReq", ws.toString());
                            System.out.println("客户端CMD:" + ws);
                        }
                    } else {
                        sockJSSocket.write("服务器已关闭。。。");
                        sockJSSocket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }


}
