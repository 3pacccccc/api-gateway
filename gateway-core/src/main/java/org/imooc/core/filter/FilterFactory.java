package org.imooc.core.filter;

import org.imooc.core.context.GatewayContext;

/**
 * @author maruimin
 * @date 2023/7/8 22:58
 */
public interface FilterFactory {

    /**
     * 构建过滤器链条
     *
     * @param ctx 网关上下文
     * @return 构建好的过滤器链条
     * @throws Exception 异常
     */
    GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception;

    /**
     * 通过过滤器ID获取过滤器
     *
     * @param filterId 过滤器id
     * @param <T>      过滤器类型
     * @return 过滤器id对应的过滤器
     * @throws Exception 异常
     */
    <T> T getFilterInfo(String filterId) throws Exception;
}
