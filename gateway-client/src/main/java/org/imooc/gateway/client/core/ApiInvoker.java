package org.imooc.gateway.client.core;

/**
 * 必须要在方法上面强制声明
 *
 * @author maruimin
 * @date 2023/5/31 21:05
 */
public @interface ApiInvoker {

    String path();
}
