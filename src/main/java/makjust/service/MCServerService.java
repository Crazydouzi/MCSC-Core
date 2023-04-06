package makjust.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import makjust.entity.MCServer;
import makjust.entity.MCServerConfigFile;
import makjust.entity.MCSetting;

public interface MCServerService {
    //核心基本配置设定
    void setCoreSetting( MCSetting setting,Handler<AsyncResult<JsonObject>> resultHandler);

    //服务器配置设定
    void setServerSetting( MCServer mcServer,Handler<AsyncResult<JsonObject>> resultHandler);

    //获取设置
    void getSetting(Vertx vertx, MCSetting mcSetting, Handler<AsyncResult<JsonObject>> resultHandler);
    //获取服务器配置文件列表
    void getConfigFileList(Vertx vertx,MCServer mcServer,Handler<AsyncResult<JsonObject>> resultHandler);
    //读取配置文件(目前仅支持yml，txt[eula only],properties)
    void readConfigFile(Vertx vertx,MCServer mcServer, MCServerConfigFile configFile, Handler<AsyncResult<JsonObject>> resultHandler);
    //读取插件列表
    void getPluginList(Vertx vertx,MCServer mcServer,Handler<AsyncResult<JsonObject>> resultHandler);
    //停用插件(修改文件命后缀为disable)
    void disablePlugins(Vertx vertx,MCServer mcServer,String plugin,Handler<AsyncResult<JsonObject>> resultHandler);
    //启用插件(修改文件名后缀无)
    void enablePlugins(Vertx vertx,MCServer mcServer,String plugin,Handler<AsyncResult<JsonObject>> resultHandler);
    //插件上传
    //开启服务器
    void serverStart(Vertx vertx, Handler<AsyncResult<JsonObject>> resultHandler);

    //关闭服务器
    boolean serverStop(Vertx vertx);
    boolean serverStatus();

    //获取可用服务器
    void getEnableServer(Handler<AsyncResult<JsonObject>> resultHandler);
}
