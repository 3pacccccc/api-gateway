package org.imooc.gateway.config.center.api;

/**
 * @author maruimin
 * @date 2023/6/2 22:14
 */
public interface ConfigCenter {

    /**
     * 初始化
     *
     * @param serverAddr 服务地址
     * @param env        环境
     */
    void init(String serverAddr, String env);

    /**
     * 监听规则变化
     *
     * @param listener 监听器
     */
    void subscribeRulesChange(RulesChangeListener listener);
}
