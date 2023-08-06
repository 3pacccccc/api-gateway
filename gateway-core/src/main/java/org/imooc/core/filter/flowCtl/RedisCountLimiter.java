package org.imooc.core.filter.flowCtl;

import lombok.extern.slf4j.Slf4j;
import org.imooc.core.Redis.JedisUtil;

/**
 * 使用Redis实现分布式限流
 *
 * @author maruimin
 * @date 2023/7/17 13:35
 */

@Slf4j
public class RedisCountLimiter {

    protected JedisUtil jedisUtil;

    public RedisCountLimiter(JedisUtil jedisUtil) {
        this.jedisUtil = jedisUtil;
    }

    private static final int SUCCESS_RESULT = 1;
    private static final int FAILED_RESULT = 0;

    public boolean doFlowCtl(String key, int limit, int expire) {
        try {
            Object object = jedisUtil.executeScript(key, limit, expire);
            if (object == null) {
                return true;
            }

            Long result = Long.valueOf(object.toString());
            if (FAILED_RESULT == result) {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("分布式限流发生错误，e:", e);
        }
        return true;
    }
}
