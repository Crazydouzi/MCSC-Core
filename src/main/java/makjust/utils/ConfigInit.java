package makjust.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

public class ConfigInit {
    private void mkResourcesDIR() throws IOException {
        String directoryPath = getConfig.getBasePath();
        File file = new File(directoryPath);
        if (!file.exists()){
            //创建文件夹
            if (file.mkdirs()){
                System.out.println("创建" + directoryPath + "成功");
            } else {
                System.out.println("创建" + directoryPath + "失败");
            }
        }
//        String filePath = directoryPath + "\\hello.txt";
//        File file2 = new File(filePath);
//        if (file2.exists()){
//            System.out.println("该文件已存在,不能重复创建");
//        } else {
//            //创建文件
//            if (file2.createNewFile()){
//                System.out.println("文件创建成功");
//                //写入内容
//                BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
//                writer.write("hello,world~ 编程快乐");
//                writer.close();
//            } else {
//                System.out.println("文件创建失败");
//            }
//        }
    }

    public ConfigInit() throws IOException {
      this.mkResourcesDIR();
    }
}
