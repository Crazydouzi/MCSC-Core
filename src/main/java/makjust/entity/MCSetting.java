package makjust.entity;

import lombok.Data;

@Data
public class MCSetting {
    private Integer id;
    private Integer serverId;
    private String option;
    private String value;
    private Boolean enable;

}
