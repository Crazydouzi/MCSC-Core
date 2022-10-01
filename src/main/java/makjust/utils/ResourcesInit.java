package makjust.utils;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public class ResourcesInit {
    private URI path = ResourcesInit.class.getProtectionDomain().getCodeSource().getLocation().toURI();
    private String resourcesPath = path.getPath() + "/resources";
    private List<String> checkList=Arrays.asList("config.yml","package");
    public ResourcesInit() throws Exception {
        this.mkResourcesDIR();
    }
    private void mkResourcesDIR() throws Exception {
        System.out.println("URI:" + path);
        if (getENV()) {
            resourcesPath = new StringBuilder(path.getPath()).substring(0, (path.getPath().lastIndexOf("/"))) + "/resources";
            System.out.println("resourcesPath" + resourcesPath);
            copyJarResourcesFileToTemp(path, resourcesPath, "resources");

        } else {
            checkConfig(resourcesPath);
            copyLocalResourcesFileToTemp(new File(path.getPath()), new File(resourcesPath), true);

        }

    }

    //用于IDEA开发时复制resources
    private static void copyLocalResourcesFileToTemp(File f, File nf, boolean flag) throws Exception {
        // 判断是否存在
        if (f.exists()) {
            // 判断是否是目录
            if (f.isDirectory()) {
                if (flag) {
                    // 制定路径，以便原样输出
                    // 判断文件夹是否存在，不存在就创建
                    if (!nf.exists()) {
                        if (nf.mkdirs()){
                            System.out.println("创建文件夹"+nf.getPath());
                        }
                    }
                }
                flag = true;
                // 获取文件夹下所有的文件及子文件夹
                File[] l = f.listFiles();
                // 判断是否为null
                if (null != l) {
                    for (File ll : l) {
                        if (ll.isDirectory()) continue;
                        // 循环递归调用
                        copyLocalResourcesFileToTemp(ll, nf, flag);
                    }
                }
            } else {
                System.out.println("正在复制：" + f.getAbsolutePath());
                System.out.println("到：" + nf.getAbsolutePath() + "\\" + f.getName());
                // 获取输入流
                FileInputStream fis = new FileInputStream(f);
                // 获取输出流
                FileOutputStream fos = new FileOutputStream(nf + "/" + f.getName());
                byte[] b = new byte[1024];
                // 读取文件
                int len;
                while ((len=fis.read(b)) != -1) {
                    // 写入文件，复制
                    fos.write(b,0,len);
                }
                fos.close();
                fis.close();
            }
        }
    }

    //打jar包后复制resources
    private static void copyJarResourcesFileToTemp(URI path, String tempPath, String filePrefix) {
        try {
            List<Map.Entry<ZipEntry, InputStream>> collect =
                    readJarFile(new JarFile(path.getPath()), filePrefix).collect(Collectors.toList());
            for (Map.Entry<ZipEntry, InputStream> entry : collect) {
                // 文件相对路径
                String key = entry.getKey().getName();
                System.out.println("filePath:" + key);
                // 文件流
                InputStream stream = entry.getValue();
                File newFile = new File(tempPath + key.replaceAll("resources", ""));
                if (!newFile.getParentFile().exists()) {
                    newFile.getParentFile().mkdirs();
                }
                org.apache.commons.io.FileUtils.copyInputStreamToFile(stream, newFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Stream<Map.Entry<ZipEntry, InputStream>> readJarFile(JarFile jarFile, String prefix) {
        Stream<Map.Entry<ZipEntry, InputStream>> readingStream =
                jarFile.stream().filter(entry -> !entry.isDirectory() && entry.getName().startsWith(prefix))
                        .map(entry -> {
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

    private void checkConfig(String resourcesPath) throws IOException {
//       File file=new File(resourcesPath);
//        for (String fileName:checkList
//             ) {
//            File check=new File(resourcesPath+fileName);
//            if (check.exists())continue;
//            else {
//
//            }
//        }

    }
    //用于判断是跑idea里还是jar里
    private  Boolean getENV() {
        return path.getPath().contains(".jar");
    }


}
