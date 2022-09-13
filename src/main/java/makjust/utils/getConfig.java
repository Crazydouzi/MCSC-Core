package makjust.utils;

public class getConfig {
    public static String path=Class.class.getResource("/").getPath()+"mcPackage";
    public static String getPackageBasePath() {
        return  path;
    }
    public static String getCorePath(String version) {
        return  path+version;
    }
}
