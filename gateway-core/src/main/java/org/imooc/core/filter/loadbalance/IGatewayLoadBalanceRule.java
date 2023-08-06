package org.imooc.core.filter.loadbalance;

import org.imooc.common.config.ServiceInstance;
import org.imooc.core.context.GatewayContext;

/**
 * @author maruimin
 * @date 2023/7/9 10:53
 */
public interface IGatewayLoadBalanceRule {
    /**
     * 通过上下文参数获取服务实例
     *
     * @param ctx 网关上下文
     * @return 服务实例
     */
    ServiceInstance choose(GatewayContext ctx);

    /**
     * 通过服务ID拿到对应的服务实例
     *
     * @param serviceId 服务id
     * @return 对应的服务实例
     */
    ServiceInstance choose(String serviceId);

}
