package makjust.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
//用于反序列化时下划线转驼峰
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public class MCServer {
    private Integer id;
    // 名称
//    @Column(name = "server_name")
    private String serverName;
    // 版本
    private String version;
    // 在resource/package下位置
    private String location;
    private Boolean enable;
    // MC设置
    private List<MCSetting> setting;
}
