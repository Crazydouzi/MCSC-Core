package makjust.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;

public class ResourcesInit {
    public ResourcesInit() throws Exception {
        this.mkResourcesDIR();
    }
    private void mkResourcesDIR() throws Exception {
        URI path = ResourcesInit.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        String resourcesPath = path.getPath() + "/resources";
        System.out.println("URI:" + path);
        System.out.println("resourcesPath:" + resourcesPath);
        if (path.getPath().contains(".jar")) {
            System.out.println(path.toString() + "/" + path.getPath() + "=================>" + resourcesPath);

        } else {
            copy(new File(path.getPath()), new File(resourcesPath), true);

        }

//        copy(new File(path),new File(resourcesPath),true);
    }
    private static void copy(File f, File nf, boolean flag) throws Exception {
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
                        copy(ll, nf, flag);
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


}
