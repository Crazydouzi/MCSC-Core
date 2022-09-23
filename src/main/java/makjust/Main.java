package makjust;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import makjust.utils.getConfig;
import makjust.verticle.MCCWorkVerticle;
import makjust.verticle.MCWSVerticle;

/**
 * Created by chengen on 26/04/2017.
 */
public class Main{
    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        System.out.println("主线程启动！");
        System.out.println(getConfig.getPackageBasePath());
        System.out.println(getConfig.getCorePath("1"));

        vertx.deployVerticle(MCWSVerticle.class.getName());
        vertx.deployVerticle(MCCWorkVerticle.class.getName(),new DeploymentOptions().setWorker(true));

    }
}