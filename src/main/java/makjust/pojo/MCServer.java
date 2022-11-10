package makjust.pojo;

import lombok.Data;

import java.util.List;

@Data
public class MCServer {
    // 版本
    private String version;
    // 在resource/package下位置
    private String location;
    // 启动参数
    private String param;
    // MC设置
    private List<MCSetting> setting;
}
