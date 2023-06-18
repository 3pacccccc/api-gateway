package org.imooc.gateway.register.center.api;

import org.imooc.common.config.ServiceDefinition;
import org.imooc.common.config.ServiceInstance;

/**
 * @author maruiminb
 * @date 2023/5/27 10:52
 */
public interface RegisterCenter {
    /**
     * 初始化
     *
     * @param registerAddress 注册地址
     * @param env             环境
     */
    void init(String registerAddress, String env);

    /**
     * 注册
     *
     * @param serviceDefinition 服务定义
     * @param serviceInstance   服务实例
     */
    void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance);

    /**
     * 服务下线
     *
     * @param serviceDefinition 服务定义
     * @param serviceInstance   服务实例
     */
    void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance);

    /**
     * 订阅全部服务
     *
     * @param registerCenterListener 订阅监听器
     */
    void subscribeAllServices(RegisterCenterListener registerCenterListener);
}
