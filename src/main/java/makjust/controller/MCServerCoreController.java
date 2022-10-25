package makjust.controller;

import io.vertx.core.json.Json;
import makjust.annotation.RequestBody;
import makjust.annotation.Controller;
import makjust.pojo.MCServer;

@Controller
public class MCServerCoreController {
    // 服务器端口设定
    public Json portSetting(){
        return new Json();
    }
    // 修改MC核心启动参数
    public Json coreParamSetting(@RequestBody MCServer mcServer){
        return new Json();
    }
}
