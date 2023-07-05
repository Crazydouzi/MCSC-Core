package makjust.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import makjust.bean.MCSetting;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MCServerDTO {
    private Integer id;
    // 名称
    private String serverName;
    // 版本
    private String version;

    private Boolean enable;
    // MC设置
    private MCSetting setting;
}
