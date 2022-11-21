package makjust.service.impl;

import io.vertx.core.json.JsonObject;
import makjust.service.SystemService;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import oshi.util.Util;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Properties;

public class SystemServiceImpl implements SystemService {
    SystemInfo si = new SystemInfo();
    HardwareAbstractionLayer hal = si.getHardware();
    CentralProcessor processor = hal.getProcessor();
    Sensors sensors = hal.getSensors();
    GlobalMemory memory = hal.getMemory();
    @Override
    public JsonObject getSystemInfo() throws UnknownHostException {
        Properties props = System.getProperties();
        InetAddress addr;
        addr = InetAddress.getLocalHost();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("HostName", addr.getHostName());
        jsonObject.put("HostAddress", addr.getHostAddress());
        jsonObject.put("cpuName", processor.getProcessorIdentifier().getName());
        jsonObject.put("cpuCoreCount", processor.getPhysicalProcessorCount() + "核心/" + processor.getLogicalProcessorCount() + "线程");
        jsonObject.put("CpuTemperature", sensors.getCpuTemperature() > 0 ? sensors.getCpuTemperature() : "Sensors Error");
        jsonObject.put("memoryInfo", memory.toString().replace("Available:", ""));
        jsonObject.put("totalMemory", String.format("%.1f", memory.getTotal() / 1073741824.0) + " GiB");
        jsonObject.put("freeMemory", String.format("%.1f", memory.getAvailable() / 1073741824.0) + " GiB");
        jsonObject.put("javaVersion", props.getProperty("java.version"));
        return jsonObject;
    }
    // 这玩意根本不准
    public JsonObject getCpuUsage() {
        JsonObject jsonObject = new JsonObject();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        System.out.println("----------------cpu信息----------------");
        System.out.println("cpu核数:" + processor.getLogicalProcessorCount());
        System.out.println("cpu系统使用率:" + new DecimalFormat("#.##%").format(cSys * 1.0 / totalCpu));
        System.out.println("cpu用户使用率:" + new DecimalFormat("#.##%").format(user * 1.0 / totalCpu));
        System.out.println("cpu当前等待率:" + new DecimalFormat("#.##%").format(iowait * 1.0 / totalCpu));
        System.out.println("cpu当前使用率:" + new DecimalFormat("#.##%").format(1.0 - (idle * 1.0 / totalCpu)));
        jsonObject.put("cpuCoreCount", processor.getLogicalProcessorCount());
        jsonObject.put("cpuSysUsage", new DecimalFormat("#.##%").format(cSys * 1.0 / totalCpu));
        jsonObject.put("cpuUserUsage", new DecimalFormat("#.##%").format(user * 1.0 / totalCpu));
        jsonObject.put("cpuWaitPer", new DecimalFormat("#.##%").format(iowait * 1.0 / totalCpu));
        jsonObject.put("cpuUsage", new DecimalFormat("#.##%").format(1.0 - (idle * 1.0 / totalCpu)));
        return jsonObject;
    }

    @Override
    public JsonObject getMemoryUsage() {
        System.out.println("----------------主机内存信息----------------");
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        //总内存
        long totalByte = memory.getTotal();
        //剩余
        long acaliableByte = memory.getAvailable();
        System.out.println("总内存 = " + formatByte(totalByte));
        System.out.println("使用" + formatByte(totalByte - acaliableByte));
        System.out.println("剩余内存 = " + formatByte(acaliableByte));
        System.out.println("使用率：" + new DecimalFormat("#.##%").format((totalByte - acaliableByte) * 1.0 / totalByte));
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("memTotal", formatByte(totalByte));
        jsonObject.put("memUse", formatByte(acaliableByte));
        jsonObject.put("memFree", formatByte(acaliableByte));
        jsonObject.put("memUsage", new DecimalFormat("#.##%").format((totalByte - acaliableByte) * 1.0 / totalByte));
        return jsonObject;
    }

    public static String formatByte(long byteNumber) {
        //换算单位
        double FORMAT = 1024.0;
        double kbNumber = byteNumber / FORMAT;
        if (kbNumber < FORMAT) {
            return new DecimalFormat("#.##KB").format(kbNumber);
        }
        double mbNumber = kbNumber / FORMAT;
        if (mbNumber < FORMAT) {
            return new DecimalFormat("#.##MB").format(mbNumber);
        }
        double gbNumber = mbNumber / FORMAT;
        if (gbNumber < FORMAT) {
            return new DecimalFormat("#.##GB").format(gbNumber);
        }
        double tbNumber = gbNumber / FORMAT;
        return new DecimalFormat("#.##TB").format(tbNumber);
    }

}
