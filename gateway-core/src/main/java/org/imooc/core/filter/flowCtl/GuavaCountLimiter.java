package org.imooc.core.filter.flowCtl;

import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang3.StringUtils;
import org.imooc.common.rule.Rule;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author maruimin
 * @date 2023/7/17 14:06
 */
public class GuavaCountLimiter {

    private RateLimiter rateLimiter;

    private double maxPermits;

    public GuavaCountLimiter(double maxPermits) {
        this.rateLimiter = RateLimiter.create(maxPermits);
        this.maxPermits = maxPermits;
    }

    public GuavaCountLimiter(long warmUpPeriodAsSecond, double maxPermits) {
        this.maxPermits = maxPermits;
        this.rateLimiter = RateLimiter.create(maxPermits, warmUpPeriodAsSecond, TimeUnit.SECONDS);
    }

    public static ConcurrentHashMap<String, GuavaCountLimiter> resourceRateLimiterMap = new ConcurrentHashMap<>();

    public static GuavaCountLimiter getInstance(String serviceId, Rule.FlowCtlConfig flowCtlConfig) {
        if (StringUtils.isEmpty(serviceId)
                || StringUtils.isEmpty(flowCtlConfig.getValue())
                || StringUtils.isEmpty(flowCtlConfig.getConfig())
                || StringUtils.isEmpty(flowCtlConfig.getType())) {
            return null;
        }
        String key = serviceId + "." + flowCtlConfig.getValue();
        GuavaCountLimiter countLimiter = resourceRateLimiterMap.get(key);
        if (countLimiter == null) {
            countLimiter = new GuavaCountLimiter(50);
            resourceRateLimiterMap.put(key, countLimiter);
        }
        return countLimiter;
    }

    public boolean acquire(int permits) {
        return rateLimiter.tryAcquire(permits);
    }
}
