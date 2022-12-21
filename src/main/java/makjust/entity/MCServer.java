package makjust.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
