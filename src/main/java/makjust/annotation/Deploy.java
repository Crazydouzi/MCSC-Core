package makjust.annotation;

import io.vertx.core.DeploymentOptions;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Deploy {
    // 开启worker模式
    boolean worker() default false;
    // 部署实例数 0为默认
    int instance() default  DeploymentOptions.DEFAULT_INSTANCES;
}
