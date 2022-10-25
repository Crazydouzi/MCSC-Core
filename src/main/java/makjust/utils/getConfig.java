package makjust.utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.yaml.snakeyaml.Yaml;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

public class getConfig {
    private static URL pathURL = getConfig.class.getProtectionDomain().getCodeSource().getLocation();
    public static JsonObject object = ConfigInit();

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

    private static JsonObject ConfigInit() {
        Yaml yaml = new Yaml();
        Map<String, Object> ret = (yaml.load(Vertx.vertx().fileSystem().readFileBlocking(getBasePath() + "resources/" + "config.yml").toString()));
        return new JsonObject(ret);
    }

    public static String getCorePath(String version) throws URISyntaxException {
        String path = pathURL.toURI().getPath();
        if (getENV()){
            path = new StringBuilder(path).substring(0, (path.lastIndexOf("/")));
            return path + "/resources/package/"+version;

        }
        return  path+"package" + version;
    }

    public static String getStaticPath() throws URISyntaxException {
        String path = pathURL.toURI().getPath();
        if (getENV()) {
            path = new StringBuilder(path).substring(0, (path.lastIndexOf("/")));
            return path + "/resources/webroot/";

        } else {
            return "webroot";
        }

    }
    public static JsonObject getCoreConf(){
        return object.getJsonObject("core");
    }
    public static JsonObject getMCConf(){
        return object.getJsonObject("mcServer");
    }
    public static Object getConf(String arg0){
        String[] arg=arg0.split("\\.");
        return object.getJsonObject(arg[0]).getString(arg[1]);
    }
    public static int getHttpServerPort() {
        return object.getJsonObject("core").getInteger("port");
    }

}
