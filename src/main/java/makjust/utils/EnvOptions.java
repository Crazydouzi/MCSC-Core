package makjust.utils;

public class EnvOptions {
    private static boolean serverStatus=false;

    public static boolean getServerStatus() {
        return serverStatus;
    }

    public static void setServerStatus(boolean serverStatus) {
        EnvOptions.serverStatus = serverStatus;
    }

}
