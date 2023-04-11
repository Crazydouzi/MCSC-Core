package makjust.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public class MCSetting {
    private Integer id;
    private Integer serverId;
    private String javaVersion;
    private String memMin;
    private String memMax;
    private String vmOptions;
    private String jarName;

    public String returnCMD() {
        if (vmOptions == null) {
            return javaVersion + " -Xmx" +memMax + " -Xms" + memMin + " -jar " + jarName;
        } else {
            return javaVersion + " Xmx"+memMax + " Xms"+memMin + " " + vmOptions + " -jar " + jarName;
        }

    }

}
