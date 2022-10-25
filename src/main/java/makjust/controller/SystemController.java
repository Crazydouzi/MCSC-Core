package makjust.controller;

import io.vertx.core.json.Json;
import makjust.annotation.RestController;

@RestController
public class SystemController {
    // 使用率报告
    public Json usageInfo(){
        return new Json();
    }

}
