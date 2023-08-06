package org.imooc.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.imooc.common.rule.Rule;
import org.imooc.core.context.GatewayContext;
import org.imooc.core.filter.router.RouterFilter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author maruimin
 * @date 2023/7/9 10:07
 */

@Slf4j
public class GatewayFilterChainFactory implements FilterFactory {

    private static class SingletonInstance {
        private static final GatewayFilterChainFactory INSTANCE = new GatewayFilterChainFactory();
    }

    public static GatewayFilterChainFactory getInstance() {
        return SingletonInstance.INSTANCE;
    }

    private final Map<String, Filter> processFilterIdMap = new ConcurrentHashMap<>();

    public GatewayFilterChainFactory() {
        ServiceLoader<Filter> serviceLoader = ServiceLoader.load(Filter.class);
        serviceLoader.stream().forEach(filterProvider -> {
            Filter filter = filterProvider.get();
            FilterAspect annotation = filter.getClass().getAnnotation(FilterAspect.class);
            log.info("load filter success:{},{},{},{}", filter.getClass(),
                    annotation.id(), annotation.name(), annotation.order());
            String filterId = annotation.id();
            if (StringUtils.isEmpty(filterId)) {
                filterId = filter.getClass().getName();
            }
            processFilterIdMap.put(filterId, filter);
        });
    }

    /**
     * 构建过滤器链条
     *
     * @param ctx 网关上下文
     * @return 构建好的过滤器链条
     * @throws Exception 异常
     */
    @Override
    public GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception {
        GatewayFilterChain chain = new GatewayFilterChain();
        List<Filter> filters = new ArrayList<>();
        Rule rule = ctx.getRule();
        if (rule != null) {
            Set<Rule.FilterConfig> filterConfigs = rule.getFilterConfigs();
            for (Rule.FilterConfig filterConfig : filterConfigs) {
                String filterId = filterConfig.getId();
                if (StringUtils.isNotEmpty(filterId) && getFilterInfo(filterId) != null) {
                    Filter filter = getFilterInfo(filterId);
                    filters.add(filter);
                }
            }
        }

        // todo 添加路由过滤器-这是最后一步
        // todo 动态判断是添加http的filter还是dubbo的filter
        filters.add(new RouterFilter());
        // 排序
        filters.sort(Comparator.comparingInt(Filter::getOrder));
        // 添加到链表中
        chain.addFilterList(filters);
        return chain;
    }

    /**
     * 通过过滤器ID获取过滤器
     *
     * @param filterId 过滤器id
     * @return 过滤器id对应的过滤器
     * @throws Exception 异常
     */
    @Override
    public Filter getFilterInfo(String filterId) throws Exception {
        return processFilterIdMap.get(filterId);
    }
}
