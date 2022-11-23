package makjust.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Request {
    String value() default "";
    HttpMethod[] method() default {};
}
