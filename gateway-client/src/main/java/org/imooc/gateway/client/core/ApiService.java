package org.imooc.gateway.client.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务定义注解
 *
 * @author maruimin
 * @date 2023/5/31 20:57
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiService {

    String serviceId();

    String version() default "1.0.0";

    ApiProtocol protocol();

    String patterPath();
}
