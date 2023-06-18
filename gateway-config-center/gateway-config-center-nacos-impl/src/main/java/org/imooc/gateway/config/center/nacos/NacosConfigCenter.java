package org.imooc.gateway.config.center.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.imooc.common.rule.Rule;
import org.imooc.gateway.config.center.api.ConfigCenter;
import org.imooc.gateway.config.center.api.RulesChangeListener;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author maruimin
 * @date 2023/6/9 22:41
 */

@Slf4j
public class NacosConfigCenter implements ConfigCenter {
    private static final String DATA_ID = "api-gateway";

    private String serverAddr;

    private String env;

    private ConfigService configService;


    /**
     * 初始化
     *
     * @param serverAddr 服务地址
     * @param env        环境
     */
    @Override
    public void init(String serverAddr, String env) {
        this.serverAddr = serverAddr;
        this.env = env;

        try {
            configService = NacosFactory.createConfigService(serverAddr);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 监听规则变化
     *
     * @param listener 监听器
     */
    @Override
    public void subscribeRulesChange(RulesChangeListener listener) {
        try {
            // 初始化通知
            String config = configService.getConfig(DATA_ID, env, 5000);
            //{"rules":[{}, {}]}
            log.info("config from nacos:{}", config);
            List<Rule> rules = JSON.parseObject(config).getJSONArray("rules").toJavaList(Rule.class);
            listener.onRulesChange(rules);

            // 监听变化
            configService.addListener(DATA_ID, env, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("config from nacos: {}", configInfo);
                    List<Rule> rules = JSON.parseObject(configInfo).getJSONArray("rules").toJavaList(Rule.class);
                    listener.onRulesChange(rules);                }
            });
        } catch (NacosException e) {
            throw new RuntimeException();
        }
    }
}
