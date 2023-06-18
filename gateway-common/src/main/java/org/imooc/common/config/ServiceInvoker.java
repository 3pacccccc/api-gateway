package org.imooc.common.config;

/**
 * @author maruimin
 * @date 2023/5/19 20:43
 */
public interface ServiceInvoker {

    /**
     * 获取真正的服务调用的全路径
     */
    String getInvokerPath();

    void setInvokerPath(String invokerPath);

    /**
     * 获取该服务调用(方法)的超时时间
     */
    int getTimeout();

    void setTimeout(int timeout);


}
