package org.imooc.core.netty.processor;

import org.imooc.core.context.HttpRequestWrapper;

/**
 * @author maruimin
 * @date 2023/5/19 20:22
 */
public interface NettyProcessor {

    /**
     * 处理请求
     *
     * @param wrapper http请求包装对象
     */
    void process(HttpRequestWrapper wrapper);

}
