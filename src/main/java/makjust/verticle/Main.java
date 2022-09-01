package makjust.verticle;

import io.vertx.core.Vertx;

/**
 * Created by chengen on 26/04/2017.
 */
public class Main{
    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();
        System.out.println("主线程启动！");
        vertx.deployVerticle(MCCVerticle.class.getName());
    }
}