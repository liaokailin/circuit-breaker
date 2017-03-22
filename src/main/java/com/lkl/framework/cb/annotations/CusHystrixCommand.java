package com.lkl.framework.cb.annotations;

import java.lang.annotation.*;

/**
 * 该注解用来指定方法需要熔断、降级等功能
 *
 * Created by liaokailin on 16/10/20.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CusHystrixCommand {

    /**
     * 指定commandKey,默认使用类名称.方法名表示
     * @return
     */
    String value() default "";
}
