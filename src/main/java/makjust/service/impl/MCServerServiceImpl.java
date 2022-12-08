package makjust.service.impl;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import makjust.utils.EnvOptions;
import makjust.serverCore.ProcessServer;
import makjust.service.MCServerService;
import makjust.utils.SysConfig;

import java.io.File;
import java.io.IOException;

public class MCServerServiceImpl implements MCServerService {
    private String DIR = SysConfig.getCorePath("/");
    private String CMD = SysConfig.object.getJsonObject("mcServer").getString("def_cmd");
    private ProcessServer mcServer;

    public MCServerServiceImpl() {
    }

    @Override
    public JsonObject editSetting() {
        return new JsonObject();
    }

    @Override
    public JsonObject getSetting() {
        return new JsonObject();
    }

    @Override
    public boolean serverStart(Vertx vertx) {
        boolean serverStatus = EnvOptions.getServerStatus();
        // 如果已创建Process(启动服务器)则直接跳过
        if (serverStatus) return false;
        mcServer = new ProcessServer(new File(DIR), CMD, vertx);
        try {
            mcServer.start();
            EnvOptions.setServerStatus(true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean serverStop(Vertx vertx) {
        boolean serverStatus = EnvOptions.getServerStatus();
        if (serverStatus&&mcServer!=null) {
            try {
                mcServer.stop();
                EnvOptions.setServerStatus(false);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
