package makjust.utils;

import io.vertx.core.Vertx;

import java.io.*;
import java.net.URI;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public class ResourcesInit {
    //    根路径
    private final URI path = ResourcesInit.class.getProtectionDomain().getCodeSource().getLocation().toURI();
    //    目标资源地址
    private String resourcesPath = path.getPath() + "resources";

    public ResourcesInit(Vertx vertx) throws Exception {
        this.copyResources();
    }

    public ResourcesInit(String... file) throws Exception {
        for (String f : file) {
            this.copyResources(f);
        }
    }

    public ResourcesInit() throws Exception {
    }

    //复制某一个文件（文件夹）

    /**
     * /复制某一个文件
     *
     * @param file 文件名
     *             默认为父级文件夹(config/package/plugins)
     */
    public void copyResources(String file) throws Exception {
        resourcesPath = new StringBuilder(path.getPath()).substring(0, (path.getPath().lastIndexOf("/"))) + "/resources/";
        if (getENV()) {
            copyJarResourcesFileToTemp(path, resourcesPath, file);
        } else {
            copyLocalResourcesFileToTemp(new File(path.getPath() + "/" + file), new File(resourcesPath));
        }
    }

    //复制全部
    private void copyResources() throws Exception {
        System.out.println("URI:" + path);
        if (getENV()) {
            resourcesPath = new StringBuilder(path.getPath()).substring(0, (path.getPath().lastIndexOf("/"))) + "/resources/";
            copyJarResourcesFileToTemp(path, resourcesPath, "webroot");
            copyJarResourcesFileToTemp(path, resourcesPath, "plugins");
            copyJarResourcesFileToTemp(path, resourcesPath, "package");
            copyJarResourcesFileToTemp(path, resourcesPath, "config");

        } else {
            copyLocalResourcesFileToTemp(new File(path.getPath()), new File(resourcesPath));
        }
    }

    /**
     * 用于IDEA开发时复制resources
     *
     * @param f  来源
     * @param nf 目标
     */
    private static void copyLocalResourcesFileToTemp(File f, File nf) throws Exception {
        System.out.println(f.getPath() + "-----------------------" + nf.getPath());
        // 判断是否存在
        if (f.exists()) {
            if (f.isDirectory()) {
                File[] array = f.listFiles();
                assert array != null;
                if (!nf.exists()) {
                    if (nf.mkdirs()) {
                        System.out.println("create direct:" + nf.getName());
                    }
                } else {
                    return;
                }
                for (File file : array) {
                    File cf = new File(nf.getAbsolutePath() + "/" + file.getName());
                    if (!cf.exists()) {
                        if (file.isDirectory()) {
                            System.out.println("copy:" + file.getPath() + "\n       to------>" + cf.getPath());
                            copyLocalResourcesFileToTemp(new File(file.getPath()), new File(cf.getAbsolutePath()));
                            continue;
                        }
                        //复制文件
                        System.out.println("正在复制：" + file.getAbsolutePath());
                        System.out.println("到：" + nf.getAbsolutePath() + "\\" + file.getName());
                        // 获取输入流
                        FileInputStream fis = new FileInputStream(file);
                        // 获取输出流
                        FileOutputStream fos = new FileOutputStream(nf + "/" + file.getName());
                        byte[] b = new byte[1024];
                        // 读取文件
                        int len;
                        while ((len = fis.read(b)) != -1) {
                            // 写入文件，复制
                            fos.write(b, 0, len);
                        }
                        fos.close();
                        fis.close();
                    }
                }
            }
        }
    }

    /**
     * 用于打包后复制resources
     *
     * @param path       来源
     * @param tempPath   目标
     * @param filePrefix 地址
     */
    private static void copyJarResourcesFileToTemp(URI path, String tempPath, String filePrefix) {
        try {
            List<Map.Entry<ZipEntry, InputStream>> collect = readJarFile(new JarFile(path.getPath()), filePrefix).collect(Collectors.toList());
            for (Map.Entry<ZipEntry, InputStream> entry : collect) {
                // 文件相对路径
                String key = entry.getKey().getName();
                // 文件流
                InputStream stream = entry.getValue();
                File newFile = new File(tempPath + key.replaceAll("resources", ""));
                if (!newFile.getParentFile().exists()) {
                    if (newFile.getParentFile().mkdirs()) {
                        System.out.println("create direct:" + newFile.getParentFile().getName());
                    }
                }
                if (newFile.exists()) continue;
                System.out.println("复制jar包资源：" + key + newFile.getPath());
                org.apache.commons.io.FileUtils.copyInputStreamToFile(stream, newFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Stream<Map.Entry<ZipEntry, InputStream>> readJarFile(JarFile jarFile, String prefix) {
        Stream<Map.Entry<ZipEntry, InputStream>> readingStream = jarFile.stream().filter(entry -> !entry.isDirectory() && entry.getName().startsWith(prefix)).map(entry -> {
            try {
                return new AbstractMap.SimpleEntry<>(entry, jarFile.getInputStream(entry));
            } catch (IOException e) {
                return new AbstractMap.SimpleEntry<>(entry, null);
            }
        });
        return readingStream.onClose(() -> {
            try {
                jarFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    //用于判断是跑idea里还是jar里
    private Boolean getENV() {
        return path.getPath().contains(".jar");
    }


}
