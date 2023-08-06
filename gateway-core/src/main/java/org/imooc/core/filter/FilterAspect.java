package org.imooc.core.filter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 过滤器注解类
 *
 * @author maruimin
 * @date 2023/7/8 22:52
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FilterAspect {

    /**
     * @return 过滤器id
     */
    String id();

    /**
     * @return 过滤器名称
     */
    String name() default "";

    /**
     * @return 过滤器排序
     */
    int order() default 0;

}
