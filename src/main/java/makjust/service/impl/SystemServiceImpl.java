package makjust.service.impl;

import io.vertx.core.json.JsonObject;
import makjust.service.SystemService;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.NetworkParams;
import oshi.software.os.OperatingSystem;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;

public class SystemServiceImpl implements SystemService {
    SystemInfo si = new SystemInfo();
    HardwareAbstractionLayer hal = si.getHardware();
    CentralProcessor cpu = hal.getProcessor();
    Sensors sensors = hal.getSensors();
    GlobalMemory memory = hal.getMemory();
    @Override
    public JsonObject getSystemInfo() throws UnknownHostException {
        Properties props = System.getProperties();
        InetAddress addr;
        addr = InetAddress.getLocalHost();

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("HostName",addr.getHostName());
        jsonObject.put("HostAddress", addr.getHostAddress());
        jsonObject.put("CPUName", cpu.getProcessorIdentifier().getName());
        jsonObject.put("CPUCoreCount", cpu.getPhysicalProcessorCount() + "核心/" + cpu.getLogicalProcessorCount() + "线程");
        jsonObject.put("CpuTemperature", sensors.getCpuTemperature() > 0 ? sensors.getCpuTemperature() : "Sensors Error");
        jsonObject.put("memoryInfo", memory.toString().replace("Available:", ""));
        jsonObject.put("totalMemory", String.format("%.1f", memory.getTotal() / 1073741824.0) + " GiB");
        jsonObject.put("freeMemory", String.format("%.1f", memory.getAvailable() / 1073741824.0) + " GiB");
        jsonObject.put("javaVersion", props.getProperty("java.version"));
        return jsonObject;
    }

    public JsonObject getCpuUsage(){
        long[] prevTicks = cpu.getSystemCpuLoadTicks();
        long[] ticks = cpu.getSystemCpuLoadTicks();
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
        return new JsonObject();
    }

}
