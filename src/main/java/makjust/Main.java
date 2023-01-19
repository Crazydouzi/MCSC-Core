package makjust;

import io.vertx.core.Vertx;
import makjust.annotation.Deploy;
import makjust.route.AbstractRoute;
import makjust.utils.ClassScanUtil;
import makjust.utils.DBUtils;
import makjust.utils.ResourcesInit;
import makjust.utils.SysConfig;

import java.util.Set;


/**
 * Created by chengen on 26/04/2017.
 */

public class Main {
    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        System.out.println("主线程启动！");
        SysConfig.vertx=vertx;
        new ResourcesInit(vertx);
        // 关闭vert.x内置DNS
        System.getProperties().setProperty("vertx.disableDnsResolver", "true");
        Set<Class<?>> classes = ClassScanUtil.scanByAnnotation("makjust.verticle", Deploy.class);
        AbstractRoute.vertx=vertx;
        DBUtils.conn(vertx);
//        for (Class<?> cls : classes) {
//            Deploy deployAnnotation = cls.getAnnotation(Deploy.class);
//            DeploymentOptions options = new DeploymentOptions();
//            boolean worker = deployAnnotation.worker();
//            int instance = deployAnnotation.instance();
//            options.setWorker(worker);
//            options.setInstances(instance);
//            vertx.deployVerticle(cls.getName(), options);
//        }
    }
}