# MCSC(MCServerControl)
基于Vert.x与Vue打造的用于Minecraft服务器的Web控制台<br/>
开即用.jpg
# 使用说明
- 打包成jar文件后可直接运行，默认端口为8080，账户名为admin，密码123123。
- 目前支持从paper直接下载服务器核心包文件。
- 运行时会解压配置文件，详情可以前往./resources/config下查看
- 可能有一堆BUG 嘻嘻
- 请注意：上传整合包解压需要消耗大量内存，请小心内存泄露。

## 未来发展
- 添加对其他源的自动获取。
- 增加文件管理。
- 实现本地容灾。让这玩意难绷(X)点。

## 开发注解
### @RoutePath
该注解标识在类上，用于标记一个路由

参数

     String value()
### @Request
该注解用于标识一个请求

参数:

    // 用于标记一个地址
    String value() default "";
    // 请求方法POST、GET等
    HttpMethod[] method() defult HttpMethod.GET;
    // 是否为异步请求
    boolean async() defult true;
请注意，当async为true时Route返回值应当为RoutingContext类型，当async为false时，返回值为JsonObject；

### @SockJSSocket("/usage")
该注解用于标记一个SockJS路由。代码示例：

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
### @RequestParam
用于解析RequestParam参数

    @Request(value = "/getConfigList", method = HttpMethod.GET)
    public RoutingContext functionName(RoutingContext ctx, @RequestParam MCServer mcServer) {
        System.out.println(mcServer);
        return ctx;
    }
### @JsonData
用于解析Json数据

     @Request(value = "/saveSystemConfig", method = HttpMethod.POST)
    public RoutingContext functionName(RoutingContext ctx,@JsonData SystemConfigDTO config) {
        System.out.println(config);
    }
    
