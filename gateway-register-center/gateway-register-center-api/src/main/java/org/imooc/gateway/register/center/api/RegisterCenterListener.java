package org.imooc.gateway.register.center.api;

import org.imooc.common.config.ServiceDefinition;
import org.imooc.common.config.ServiceInstance;

import java.util.Set;

/**
 * @author maruimin
 * @date 2023/5/27 10:58
 */
public interface RegisterCenterListener {

    /**
     * 当发生变化的时候
     *
     * @param serviceDefinition  服务定义
     * @param serviceInstanceSet 服务实例集合
     */
    void onChange(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet);
}
