package org.imooc.core.Redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author maruimin
 * @date 2023/7/17 13:36
 */

@Slf4j
public class JedisPoolUtil {

    public static JedisPool jedisPool = null;
    private String host;
    private int port;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private boolean blockWhenExhausted;
    private int maxWaitMillis;
    private boolean testOnBorrow;
    private boolean testOnReturn;

    public static Lock lock = new ReentrantLock();

    private void initialConfig() {
        try {
            Properties prop = new Properties();
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("gateway.properties"));
            host = prop.getProperty("redis.host");
            port = Integer.parseInt(prop.getProperty("redis.port"));
            maxTotal = Integer.parseInt(prop.getProperty("redis.maxTotal"));
            maxIdle = Integer.parseInt(prop.getProperty("redis.maxIdle"));
            minIdle = Integer.parseInt(prop.getProperty("redis.minIdle"));
//            blockWhenExhausted = Boolean.parseBoolean(prop.getProperty("redis.blockWhenExhausted"));
//            maxWaitMillis = Integer.parseInt(prop.getProperty("redis.maxWaitMillis"));
//            testOnBorrow = Boolean.parseBoolean(prop.getProperty("redis.testOnBorrow"));
//            testOnReturn = Boolean.parseBoolean(prop.getProperty("redis.testOnReturn"));
        } catch (Exception e) {
            log.error("parse configure file error.", e);
        }
    }

    public Jedis getJedis() {
        if (jedisPool == null) {
            initialConfig();
        }
        try {
            return jedisPool.getResource();
        } catch (Exception e) {
            log.error("getJedis error", e);
        }
        return null;
    }

    public Pipeline getPipeline() {
        BinaryJedis binaryJedis = new BinaryJedis(host, port);
        return binaryJedis.pipelined();
    }

}
