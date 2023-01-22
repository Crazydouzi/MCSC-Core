package makjust.utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.yaml.snakeyaml.Yaml;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class SysConfig {
    private static final URL pathURL = SysConfig.class.getProtectionDomain().getCodeSource().getLocation();
    public static JsonObject object = new JsonObject();

    private static Boolean getENV() {
        return pathURL.getPath().contains(".jar");
    }

    private static String getBasePath() {
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

    public void ConfigInit(Vertx vertx) {
        Yaml yaml = new Yaml();
        Map<String, Object> ret = yaml.load(Vertx.vertx().fileSystem().readFileBlocking(getBasePath() + "resources/" + "config/config.yml").toString());
        JsonObject.mapFrom(ret);
        object = JsonObject.mapFrom(ret);
    }
    //定位到MC服务器包下
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
    //定位到静态资源位置
    public static String getStaticPath() {
        String path;
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

    public static String getConf(String arg0) {
        String[] arg = arg0.split("\\.");
        return object.getJsonObject(arg[0]).getString(arg[1]);
    }

    public static int getHttpServerPort() {
        return object.getJsonObject("core").getInteger("port");
    }

}
