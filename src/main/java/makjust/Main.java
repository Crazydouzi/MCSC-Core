package makjust;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import makjust.annotation.Deploy;
import makjust.utils.ClassScanUtil;
import makjust.utils.ResourcesInit;

import java.util.Set;


/**
 * Created by chengen on 26/04/2017.
 */

public class Main {
    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        System.out.println("主线程启动！");
        new ResourcesInit();
        // 关闭vert.x内置DNS
        System.getProperties().setProperty("vertx.disableDnsResolver", "true");
        
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