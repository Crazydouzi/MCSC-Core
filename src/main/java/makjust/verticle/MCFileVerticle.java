package makjust.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;
import makjust.annotation.Deploy;
import makjust.dao.MCServerDao;
import makjust.dao.MCSettingDao;
import makjust.dao.impl.MCServerDaoImpl;
import makjust.dao.impl.MCSettingDaoImpl;
import makjust.bean.MCServer;
import makjust.bean.MCSetting;
import makjust.utils.SysConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static makjust.utils.DBPool.getLastRowId;

@Deploy(worker = true)
public class MCFileVerticle extends AbstractVerticle {
    EventBus eb;
    FileSystem fs;

    @Override
    public void start() throws Exception {
        System.out.println("挂载FILE-VERTICLE");
        eb = vertx.eventBus();
        fs = vertx.fileSystem();
        unzip();
    }

    public void unzip() {
        System.out.println("整合包处理器已挂载");
        final MCServerDao mcServerDao = new MCServerDaoImpl();
        final MCSettingDao mcSettingDao = new MCSettingDaoImpl();
        eb.consumer("file-unzip", data -> {
            JsonObject object = JsonObject.mapFrom(data.body());
            MCServer server = object.getJsonObject("server").mapTo(MCServer.class);
            MCSetting setting = object.getJsonObject("setting").mapTo(MCSetting.class);
            String tempFile = object.getString("tempFile");
            //相对位置
            String baseFileLoc = object.getString("fileLOC");
            StringBuilder fileLOC = new StringBuilder(baseFileLoc);
            fs.exists(tempFile).compose(promise -> {
                        try {
                            ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(tempFile)), Charset.forName((String) SysConfig.getConf("charset")));
                            ZipEntry zipEntry;
                            byte[] byte_s = new byte[1024];
                            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                                String fileName_zip = zipEntry.getName();
                                File file = new File(SysConfig.getCorePath(baseFileLoc) + fileName_zip);
                                if (fileName_zip.endsWith("/")) {
                                    //判断子文件夹嵌套
                                    if (fileLOC.length()==baseFileLoc.length()) {
                                        fileLOC.append("/").append(fileName_zip);
                                    }
                                    if (!file.mkdirs()) {
                                        System.out.println("创建失败");
                                    }
                                } else {
                                    BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
                                    int num;
                                    while ((num = zipInputStream.read(byte_s, 0, byte_s.length)) > 0) {
                                        outputStream.write(byte_s, 0, num);
                                    }
                                    outputStream.close();
                                }
                            }
                            zipInputStream.close();
                            return Future.succeededFuture();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return Future.failedFuture(e);
                        }
                    })
                    .compose(v1 -> fs.readDir(SysConfig.getCorePath(String.valueOf(fileLOC)), ".*(.jar)")
                    ).compose(v1 -> {
                        if (!v1.isEmpty()) {
                            v1.replaceAll(item -> item.substring(item.lastIndexOf("\\") + 1));
                            //将第一个找到的jar作为启动参数，目前无法匹配启动命令
                            setting.setJarName(v1.get(0));
                            server.setLocation(String.valueOf(fileLOC));
                            return Future.succeededFuture();
                        } else {
                            return Future.failedFuture("未发现JAR");
                        }
                    })
                    .compose(v -> mcServerDao.insertMCServer(server)
                            .compose(rows -> {
                                if (rows.rowCount() >= 1) {
                                    return getLastRowId();
                                } else {
                                    return Future.failedFuture("插入失败");
                                }
                            })
                            .compose(rows -> {
                                int id = rows.iterator().next().getInteger("id");
                                setting.setServerId(id);
                                return mcSettingDao.insertSetting(setting);
                            }).onFailure(Throwable::printStackTrace))
                    .onFailure(throwable -> {
                        throwable.printStackTrace();
                        //出现异常时删除缓存位置
                        fs.delete(SysConfig.getCorePath(baseFileLoc));
                    })
                    //完成后删除缓存文件
                    .onComplete(objectAsyncResult -> fs.delete(tempFile));
        });
    }
}
