package org.imooc.core.filter.flowCtl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.imooc.common.constants.FilterConst;
import org.imooc.common.rule.Rule;
import org.imooc.core.context.GatewayContext;
import org.imooc.core.filter.Filter;
import org.imooc.core.filter.FilterAspect;

import java.util.Set;

/**
 * 限流流控过滤器
 *
 * @author maruimin
 * @date 2023/7/17 09:36
 */

@Slf4j
@FilterAspect(id = FilterConst.FLOW_CTL_FILTER_ID,
        name = FilterConst.FLOW_CTL_FILTER_NAME,
        order = FilterConst.FLOW_CTL_FILTER_ORDER
)
public class FlowCtlFilter implements Filter {
    /**
     * 过滤
     *
     * @param ctx 网关上下文
     * @throws Exception 异常
     */
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        Rule rule = ctx.getRule();
        if (rule != null) {
            Set<Rule.FlowCtlConfig> flowCtlConfigs = rule.getFlowCtlConfigs();
            if (CollectionUtils.isNotEmpty(flowCtlConfigs)) {
                IGatewayFlowCtlRule flowCtlRule = null;
                for (Rule.FlowCtlConfig flowCtlConfig : flowCtlConfigs) {
                    String path = ctx.getRequest().getPath();
                    if (flowCtlConfig.getType().equalsIgnoreCase(FilterConst.FLOW_CTL_TYPE_PATH)
                            && path.equalsIgnoreCase(flowCtlConfig.getValue())) {
                        flowCtlRule = FlowCtlByPathRule.getInstance(rule.getServiceId(), path);

                    } else if (flowCtlConfig.getType().equalsIgnoreCase(FilterConst.FLOW_CTL_TYPE_SERVICE)) {

                    }
                    if (flowCtlRule != null) {
                        flowCtlRule.doFlowCtlFilter(flowCtlConfig, rule.getServiceId());
                    }
                }
            }
        }
    }
}
