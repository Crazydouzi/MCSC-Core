package makjust.pojo;

import lombok.Data;

import java.util.List;

@Data
public class MCServer {

    private Integer id;
    // 名称
    private String serverName;
    // 版本
    private String version;
    // 在resource/package下位置
    private String location;
    // MC设置
    private List<MCSetting> setting;
}
