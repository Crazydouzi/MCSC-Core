package makjust.verticle;

import io.vertx.core.Vertx;
import makjust.utils.getConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chengen on 26/04/2017.
 */
public class Main{
    Logger LOG = LoggerFactory.getLogger(this.getClass());
    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        System.out.println("主线程启动！");
        System.out.println(getConfig.getPackageBasePath());
        System.out.println(getConfig.getCorePath("1"));

        vertx.deployVerticle(MCWSVerticle.class.getName());
    }
}