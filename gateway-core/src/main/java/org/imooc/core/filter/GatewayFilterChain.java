package org.imooc.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.imooc.core.context.GatewayContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤器链条类
 *
 * @author maruimin
 * @date 2023/7/8 23:00
 */

@Slf4j
public class GatewayFilterChain {

    private List<Filter> filters = new ArrayList<>();

    public GatewayFilterChain addFilter(Filter filter) {
        this.filters.add(filter);
        return this;
    }

    public GatewayFilterChain addFilterList(List<Filter> filter){
        filters.addAll(filter);
        return this;
    }



    public GatewayContext doFilter(GatewayContext ctx) throws Exception {
        if (filters.isEmpty()) {
            return ctx;
        }

        try {
            for (Filter filter : filters) {
                filter.doFilter(ctx);
            }
        } catch (Exception e) {
            log.error("执行过滤器发生异常，异常信息:", e);
            throw e;
        }
        return ctx;
    }
}
