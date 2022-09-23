package makjust;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import makjust.utils.ConfigInit;
import makjust.utils.getConfig;
import makjust.verticle.MCCWorkVerticle;
import makjust.verticle.MCWSVerticle;

import java.io.IOException;

/**
 * Created by chengen on 26/04/2017.
 */
public class Main{
    public static void main(String[] args) throws IOException {
        Vertx vertx = Vertx.vertx();
        System.out.println("主线程启动！");
        new ConfigInit();
//        vertx.deployVerticle(MCWSVerticle.class.getName());
//        vertx.deployVerticle(MCCWorkVerticle.class.getName(),new DeploymentOptions().setWorker(true));

    }
}