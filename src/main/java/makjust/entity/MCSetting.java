package makjust.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.format.SnakeCase;
import io.vertx.sqlclient.templates.annotations.Column;
import io.vertx.sqlclient.templates.annotations.ParametersMapped;
import io.vertx.sqlclient.templates.annotations.RowMapped;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@DataObject
@RowMapped(formatter = SnakeCase.class)
@ParametersMapped
public class MCSetting {
    private Integer id;
//    @Column(name = "server_id")
    private Integer serverId;
//    @Column(name = "java_version")
    private String javaVersion;
//    @Column(name = "men_min")
    private String memMin;
//    @Column(name = "men_Max")
    private String memMax;
//    @Column(name = "vm_options")
    private String vmOptions;

}
