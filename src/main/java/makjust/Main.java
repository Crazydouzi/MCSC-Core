package makjust;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import makjust.annotation.Deploy;
import makjust.route.AbstractRoute;
import makjust.utils.ClassScanUtil;
import makjust.utils.DBUtils;
import makjust.utils.ResourcesInit;
import makjust.utils.SysConfig;

import java.util.Set;


public class Main {
    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        System.out.println("主线程启动！");
        new ResourcesInit(vertx);
        new SysConfig().ConfigInit(vertx);
        // 关闭vert.x内置DNS
        System.getProperties().setProperty("vertx.disableDnsResolver", "true");
        AbstractRoute.vertx=vertx;
        DBUtils.conn(vertx);
        Set<Class<?>> classes = ClassScanUtil.scanByAnnotation("makjust.verticle", Deploy.class);
        for (Class<?> cls : classes) {
            Deploy deployAnnotation = cls.getAnnotation(Deploy.class);
            DeploymentOptions options = new DeploymentOptions();
            boolean worker = deployAnnotation.worker();
            int instance = deployAnnotation.instance();
            options.setWorker(worker);
            options.setInstances(instance);
            vertx.deployVerticle(cls.getName(), options);
        }
    }
}