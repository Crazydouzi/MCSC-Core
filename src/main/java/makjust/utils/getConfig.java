package makjust.utils;

import io.vertx.core.json.JsonObject;

import java.net.URL;

public class getConfig {
    public static URL pathURL= getConfig.class.getProtectionDomain().getCodeSource().getLocation();;
    public JsonObject object;
    public static String getBasePath() {
        String path = pathURL.getPath();
        if(path.startsWith("file:")) {
            path = path.replace("file:", "");
        }
        if(path.contains(".jar")) {
            path = path.substring(0, (path.lastIndexOf("/")));
        }
        return path;
    }
    public static String getCorePath(String version) {
        return  getBasePath()+version;
    }


}
