package makjust.controller;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import makjust.annotation.Controller;
import makjust.service.MCVersionService;

@Controller
public class MCVersionController {
    MCVersionService versionService;
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
    //扫描版本
    public JsonObject scanVersion(){
        return new JsonObject();
    }
    //文件上传
    public JsonObject fileUpload(){
        return null;
    }

}
