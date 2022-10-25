package makjust.pojo;

import lombok.Data;

@Data
public class MCServer {
    // 版本
    private String version;
    // 在resource/package下位置
    private String location;
    // 启动参数
    private String param;
}
