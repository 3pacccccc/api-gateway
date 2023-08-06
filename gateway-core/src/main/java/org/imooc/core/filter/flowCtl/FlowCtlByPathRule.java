package org.imooc.core.filter.flowCtl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.imooc.common.constants.FilterConst;
import org.imooc.common.rule.Rule;
import org.imooc.core.Redis.JedisUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author maruimin
 * @date 2023/7/17 13:33
 */
public class FlowCtlByPathRule implements IGatewayFlowCtlRule {

    private String serviceId;

    private String path;

    private RedisCountLimiter redisCountLimiter;

    private static final String LIMIT_MESSAGE = "您的请求过于频繁,请稍后重试";

    public FlowCtlByPathRule(String serviceId, String path, RedisCountLimiter redisCountLimiter) {
        this.serviceId = serviceId;
        this.path = path;
        this.redisCountLimiter = redisCountLimiter;
    }

    private static ConcurrentHashMap<String, FlowCtlByPathRule> servicePathMap = new ConcurrentHashMap<>();

    public static FlowCtlByPathRule getInstance(String serviceId, String path) {
        String key = serviceId + "." + path;
        FlowCtlByPathRule flowCtlByPathRule = servicePathMap.get(key);
        if (flowCtlByPathRule == null) {
            flowCtlByPathRule = new FlowCtlByPathRule(serviceId, path, new RedisCountLimiter(new JedisUtil()));
            servicePathMap.put(key, flowCtlByPathRule);
        }
        return flowCtlByPathRule;


    }

    /**
     * 执行限流
     *
     * @param flowCtlConfig 限流配置规则
     * @param serviceId     服务id
     */
    @Override
    public void doFlowCtlFilter(Rule.FlowCtlConfig flowCtlConfig, String serviceId) {
        if (flowCtlConfig == null || StringUtils.isBlank(serviceId) || StringUtils.isEmpty(flowCtlConfig.getConfig())) {
            return;
        }

        Map<String, Integer> configMap = JSON.parseObject(flowCtlConfig.getConfig(), Map.class);
        if (!configMap.containsKey(FilterConst.FLOW_CTL_LIMIT_DURATION) || !configMap.containsKey(FilterConst.FLOW_CTL_LIMIT_PERMITS)) {
            return;
        }

        double duration = configMap.get(FilterConst.FLOW_CTL_LIMIT_DURATION);
        double permits = configMap.get(FilterConst.FLOW_CTL_LIMIT_PERMITS);
        boolean flag = true;
        String key = serviceId + "." + path;
        if (FilterConst.FLOW_CTL_MODEL_DISTRIBUTED.equalsIgnoreCase(flowCtlConfig.getModel())) {
            // 分布式限流
            flag = redisCountLimiter.doFlowCtl(key, (int) permits, (int) duration);
        } else {
            GuavaCountLimiter guavaCountLimiter = GuavaCountLimiter.getInstance(serviceId, flowCtlConfig);
            if (guavaCountLimiter == null) {
                throw new RuntimeException("获取单机限流工具类为空");
            }
            double count = Math.ceil(permits / duration);
            flag = guavaCountLimiter.acquire((int) count);
        }
        if (!flag) {
            throw new RuntimeException(LIMIT_MESSAGE);
        }
    }
}
