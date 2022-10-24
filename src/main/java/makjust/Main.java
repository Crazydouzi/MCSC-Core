package makjust;

import io.vertx.core.Vertx;
import makjust.utils.ResourcesInit;
import makjust.verticle.MainVerticle;


/**
 * Created by chengen on 26/04/2017.
 */

public class Main{
    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        System.out.println("主线程启动！");
        new ResourcesInit();
        vertx.deployVerticle(MainVerticle.class.getName());
//        vertx.deployVerticle(MCCWorkVerticle.class.getName(),new DeploymentOptions().setWorker(true));
    }
}