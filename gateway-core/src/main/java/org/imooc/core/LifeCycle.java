package org.imooc.core;

/**
 * @author maruimin
 * @date 2023/5/15 19:54
 */
public interface LifeCycle {

    /**
     * 初始化
     */
    void init();

    /**
     * 启动
     */
    void start();

    /**
     * 关闭
     */
    void shutdown();

}
