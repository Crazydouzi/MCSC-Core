package makjust.controller;

import io.vertx.core.json.Json;
import makjust.annotation.RestController;

@RestController
public class MCVersionController {
    // 获取已上传版本列表（可部署版本）
    public Json getVersionList(){
        return new Json();
    }
    //获取远程仓库可用版本
    public Json getStoreVersionList(){
        return new Json();
    }
    //配置远程仓库
    public Json setStore(){
        return new Json();
    }


}
