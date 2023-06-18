package org.imooc.core;

import lombok.extern.slf4j.Slf4j;
import org.imooc.core.netty.NettyHttpClient;
import org.imooc.core.netty.NettyHttpServer;
import org.imooc.core.netty.processor.NettyCoreProcessor;
import org.imooc.core.netty.processor.NettyProcessor;

/**
 * @author maruimin
 * @date 2023/5/19 22:58
 */
@Slf4j
public class Container implements LifeCycle{
    private final Config config;

    private NettyHttpServer nettyHttpServer;

    private NettyHttpClient nettyHttpClient;

    private NettyProcessor nettyProcessor;

    public Container(Config config) {
        this.config = config;
        init();
    }

    /**
     * 初始化
     */
    @Override
    public void init() {
        this.nettyProcessor = new NettyCoreProcessor();
        this.nettyHttpServer = new NettyHttpServer(config, nettyProcessor);
        this.nettyHttpClient = new NettyHttpClient(config, nettyHttpServer.getEventLoopGroupWorker());

    }

    /**
     * 启动
     */
    @Override
    public void start() {
        nettyHttpServer.start();
        nettyHttpClient.start();
        log.info("api gateway started!");
    }

    /**
     * 关闭
     */
    @Override
    public void shutdown() {
        nettyHttpClient.shutdown();
        nettyHttpServer.shutdown();
    }
}
