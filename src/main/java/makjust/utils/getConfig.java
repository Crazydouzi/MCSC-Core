package makjust.utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.yaml.snakeyaml.Yaml;

import java.net.URL;
import java.util.Map;
public class getConfig {
    private static URL pathURL= getConfig.class.getProtectionDomain().getCodeSource().getLocation();
    public static JsonObject object = ConfigInit();

    private static String getBasePath() {
        String path = pathURL.getPath();
        if (path.startsWith("file:")) {
            path = path.replace("file:", "");
        }
        if (path.contains(".jar")) {
            path = new StringBuilder(path).substring(0, (path.lastIndexOf("/")));
        }
        return path;
    }

    private static JsonObject ConfigInit() {
        Yaml yaml=new Yaml();
        System.out.println(getBasePath() + "resources/" + "config.yml");
        Map<String, Object> ret = (yaml.load(Vertx.vertx().fileSystem().readFileBlocking(getBasePath() + "resources/" + "config.yml").toString()));
        return new JsonObject(ret);
    }

    public static String getCorePath(String version) {
        return getBasePath() + "resources/" + version;
    }

    public static String getResourcesPath() {
        return getBasePath() + "resources";
    }

}
