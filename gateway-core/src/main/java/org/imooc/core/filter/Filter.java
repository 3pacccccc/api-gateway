package org.imooc.core.filter;

import org.imooc.core.context.GatewayContext;

/**
 * 过滤器顶级接口
 * @author maruimin
 * @date 2023/7/8 22:54
 */
public interface Filter {

    /**
     * 过滤
     * @param ctx 网关上下文
     * @throws Exception 异常
     */
    void doFilter(GatewayContext ctx) throws Exception;

    /**
     * 获取排序
     * @return 过滤器排序
     */
    default int getOrder() {
        FilterAspect annotation = this.getClass().getAnnotation(FilterAspect.class);
        if (annotation != null) {
            return annotation.order();
        }
        return Integer.MAX_VALUE;
    }
}
