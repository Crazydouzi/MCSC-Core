package makjust.utils;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.yaml.snakeyaml.Yaml;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SysConfig {
    private static final URL pathURL = SysConfig.class.getProtectionDomain().getCodeSource().getLocation();
    public static JsonObject object;


    static {
        try {
            object = ConfigInit();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    public static Vertx vertx;

    private static Boolean getENV() {
        return pathURL.getPath().contains(".jar");
    }

    public static String getBasePath() {
        String path = pathURL.getPath();
        if (getENV()) {
            path = new StringBuilder(path).substring(0, (path.lastIndexOf("/")));
            return path + "/";
        } else {
            path = path.replace("file:", "");
            return path;
        }
    }

    public static String resourcesPath() {
        return getBasePath() + "resources/";
    }

    private static JsonObject ConfigInit() throws ExecutionException{
        ConfigStoreOptions store = new ConfigStoreOptions()
                .setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject()
                        .put("path", getBasePath() + "resources/" + "config/config.yml")
                )
                ;
        ConfigRetriever retriever = ConfigRetriever.create(vertx,
                new ConfigRetrieverOptions().addStore(store));
        //        Yaml yaml = new Yaml();
//        Map<String, Object> ret = (yaml.load(Vertx.vertx().fileSystem().readFileBlocking(getBasePath() + "resources/" + "config/config.yml").toString()));
        try {
            JsonObject yaml=retriever.getConfig().toCompletionStage().toCompletableFuture().get();
            System.out.println(yaml);
            return yaml;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getCorePath(String version) {
        try {
            String path = pathURL.toURI().getPath();
            if (getENV()) {
                path = new StringBuilder(path).substring(0, (path.lastIndexOf("/")));
            }
            return path + "/resources/package/" + version;
        } catch (URISyntaxException e) {
            return "";
        }

    }

    public static String getStaticPath() {
        String path = null;
        try {
            path = pathURL.toURI().getPath();
            if (getENV()) {
                path = new StringBuilder(path).substring(0, (path.lastIndexOf("/")));
                return path + "/resources/webroot/";

            } else {
                return "resources/webroot";
            }
        } catch (URISyntaxException e) {
            return "";
        }
    }

    public static JsonObject getCoreConf() {
        return object.getJsonObject("core");
    }

    public static JsonObject getMCConf() {
        return object.getJsonObject("mcServer");
    }

    public static Object getConf(String arg0) {
        String[] arg = arg0.split("\\.");
        return object.getJsonObject(arg[0]).getString(arg[1]);
    }

    public static int getHttpServerPort() {
        return object.getJsonObject("core").getInteger("port");
    }

}
