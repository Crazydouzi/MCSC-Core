package makjust.pojo;

import lombok.Data;

@Data
public class Setting {
    private Integer id;
    private String option;
    private String value;
    private Boolean enable;

}
