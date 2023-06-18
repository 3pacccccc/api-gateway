package org.imooc.gateway.config.center.api;

import org.imooc.common.rule.Rule;

import java.util.List;

/**
 * @author maruimin
 * @date 2023/6/2 22:16
 */
public interface RulesChangeListener {

    /**
     * 规则改变
     *
     * @param rules 规则列表
     */
    void onRulesChange(List<Rule> rules);
}
