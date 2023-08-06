package org.imooc.core.filter.loadbalance;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.imooc.common.config.ServiceInstance;
import org.imooc.common.constants.FilterConst;
import org.imooc.common.enums.ResponseCode;
import org.imooc.common.exception.NotFoundException;
import org.imooc.common.rule.Rule;
import org.imooc.core.context.GatewayContext;
import org.imooc.core.filter.Filter;
import org.imooc.core.filter.FilterAspect;
import org.imooc.core.request.GatewayRequest;

import java.util.Map;
import java.util.Set;

import static org.imooc.common.constants.FilterConst.LOAD_BALANCE_STRATEGY_RANDOM;
import static org.imooc.common.constants.FilterConst.LOAD_BALANCE_STRATEGY_ROUND_ROBIN;


/**
 * @author maruimin
 * @date 2023/7/9 10:54
 */

@FilterAspect(id = FilterConst.LOAD_BALANCE_FILTER_ID,
        name = FilterConst.LOAD_BALANCE_FILTER_NAME,
        order = FilterConst.LOAD_BALANCE_FILTER_ORDER
)
@Slf4j
public class LoadBalanceFilter implements Filter {
    /**
     * 过滤
     *
     * @param ctx 网关上下文
     * @throws Exception 异常
     */
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        String serviceId = ctx.getUniqueId();
        IGatewayLoadBalanceRule loadBalanceRule = getLoadBalanceRule(ctx);
        ServiceInstance serviceInstance = loadBalanceRule.choose(serviceId);
        log.info("serviceInstance:{}", JSON.toJSONString(serviceInstance));
        GatewayRequest request = ctx.getRequest();
        if (serviceInstance != null && request != null) {
            String host = serviceInstance.getIp() + ":" + serviceInstance.getPort();
            request.setModifyHost(host);
        } else {
            log.warn("No instance available for :{}", serviceId);
            throw new NotFoundException(ResponseCode.SERVICE_INSTANCE_NOT_FOUND);
        }
    }

    public IGatewayLoadBalanceRule getLoadBalanceRule(GatewayContext ctx) {
        IGatewayLoadBalanceRule loadBalanceRule = null;
        Rule configRule = ctx.getRule();
        if (configRule != null) {
            Set<Rule.FilterConfig> filterConfigs = configRule.getFilterConfigs();
            for (Rule.FilterConfig filterConfig : filterConfigs) {
                String filterId = filterConfig.getId();
                if (filterId.equals(FilterConst.LOAD_BALANCE_FILTER_ID)) {
                    String config = filterConfig.getConfig();
                    String strategy = FilterConst.LOAD_BALANCE_STRATEGY_RANDOM;
                    if (StringUtils.isNotEmpty(config)) {
                        Map<String, String> map = JSON.parseObject(config, Map.class);
                        strategy = map.getOrDefault(FilterConst.LOAD_BALANCE_KEY, strategy);
                    }
                    switch (strategy) {
                        case LOAD_BALANCE_STRATEGY_RANDOM:
                            loadBalanceRule = RandomLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                        case LOAD_BALANCE_STRATEGY_ROUND_ROBIN:
                            loadBalanceRule = RoundRobinLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                        default:
                            log.warn("No loadBalance strategy for service:{}", strategy);
                            loadBalanceRule = RandomLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                    }

                }
            }
        }
        return loadBalanceRule;
    }
}
