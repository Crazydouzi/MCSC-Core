package makjust;

import io.vertx.core.Vertx;
import makjust.utils.ResourcesInit;
import makjust.utils.getConfig;

import java.io.IOException;

/**
 * Created by chengen on 26/04/2017.
 */

public class Main{
    public static void main(String[] args) throws IOException {
        Vertx vertx = Vertx.vertx();
        System.out.println("主线程启动！");
        new ResourcesInit();
        System.out.println(getConfig.object);
        //        vertx.deployVerticle(MCWSVerticle.class.getName());
//        vertx.deployVerticle(MCCWorkVerticle.class.getName(),new DeploymentOptions().setWorker(true));

    }
}