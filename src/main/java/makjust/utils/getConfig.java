package makjust.utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.yaml.snakeyaml.Yaml;
import java.net.URL;
import java.util.Map;

public class getConfig {
    public static URL pathURL = getConfig.class.getProtectionDomain().getCodeSource().getLocation();
    public static JsonObject object = new JsonObject(ConfigInit());

    public static String getBasePath() {
        String path = pathURL.getPath();
        if (path.startsWith("file:")) {
            path = path.replace("file:", "");
        }
        if (path.contains(".jar")) {
            path = new StringBuilder(path).substring(0, (path.lastIndexOf("/")));
        }
        System.out.println(path);
        return path;
    }

    private static Map<String,Object> ConfigInit() {
        Yaml yaml=new Yaml();
        return (Map<String, Object>) yaml.load(Vertx.vertx().fileSystem().readFileBlocking("config.yml").toString());
    }

    public static String getCorePath(String version) {
        return getBasePath() + version;
    }


}
