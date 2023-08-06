package org.imooc.core.filter.flowCtl;

import org.imooc.common.rule.Rule;

/**
 * 执行限流的接口
 *
 * @author maruimin
 * @date 2023/7/17 13:32
 */
public interface IGatewayFlowCtlRule {

    /**
     * 执行限流
     *
     * @param flowCtlConfig 限流配置规则
     * @param serviceId     服务id
     */
    void doFlowCtlFilter(Rule.FlowCtlConfig flowCtlConfig, String serviceId);

}
