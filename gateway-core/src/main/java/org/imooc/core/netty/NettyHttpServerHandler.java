package org.imooc.core.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import org.imooc.core.context.HttpRequestWrapper;
import org.imooc.core.netty.processor.NettyProcessor;

/**
 * @author maruimin
 * @date 2023/5/19 20:17
 */
public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {

    private final NettyProcessor nettyProcessor;

    public NettyHttpServerHandler(NettyProcessor nettyProcessor) {
        this.nettyProcessor = nettyProcessor;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper();
        httpRequestWrapper.setRequest(request);
        httpRequestWrapper.setCtx(ctx);
        nettyProcessor.process(httpRequestWrapper);
    }
}
