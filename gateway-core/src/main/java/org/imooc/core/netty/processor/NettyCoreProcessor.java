package org.imooc.core.netty.processor;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.imooc.common.enums.ResponseCode;
import org.imooc.common.exception.BaseException;
import org.imooc.common.exception.ConnectException;
import org.imooc.common.exception.ResponseException;
import org.imooc.core.ConfigLoader;
import org.imooc.core.context.GatewayContext;
import org.imooc.core.context.HttpRequestWrapper;
import org.imooc.core.helper.AsyncHttpHelper;
import org.imooc.core.helper.RequestHelper;
import org.imooc.core.helper.ResponseHelper;
import org.imooc.core.response.GatewayResponse;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author maruimin
 * @date 2023/5/19 20:26
 */
@Slf4j
public class NettyCoreProcessor implements NettyProcessor {
    /**
     * 处理请求
     *
     * @param wrapper http请求包装对象
     */
    @Override
    public void process(HttpRequestWrapper wrapper) {
        FullHttpRequest request = wrapper.getRequest();
        ChannelHandlerContext ctx = wrapper.getCtx();
        try {
            GatewayContext gatewayContext = RequestHelper.doContext(request, ctx);
            route(gatewayContext);
        } catch (BaseException e) {
            log.error("process error {} {}", e.getCode().getCode(), e.getCode().getMessage());
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(e.getCode());
            doWriteAndRelease(ctx, request, httpResponse);
        } catch (Throwable t) {
            log.error("process unknown error", t);
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);
            doWriteAndRelease(ctx, request, httpResponse);
        }

    }

    private void doWriteAndRelease(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse httpResponse) {
        // 释放资源后关闭channel
        ctx.writeAndFlush(httpResponse)
                .addListener(ChannelFutureListener.CLOSE);
        ReferenceCountUtil.release(request);
    }

    private void route(GatewayContext gatewayContext) {
        Request request = gatewayContext.getRequest().build();
        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().executeRequest(request);

        boolean whenComplete = ConfigLoader.getConfig().isWhenComplete();
        if (whenComplete) {
            future.whenComplete((response, throwable) -> {
                complete(request, response, throwable, gatewayContext);
            });
        } else {
            future.whenCompleteAsync((response, throwable) -> {
                complete(request, response, throwable, gatewayContext);
            });
        }
    }

    private void complete(Request request, Response response, Throwable throwable, GatewayContext gatewayContext) {
        gatewayContext.releaseRequest();
        try {
            if (Objects.nonNull(throwable)) {
                String url = request.getUrl();
                if (throwable instanceof TimeoutException) {
                    log.warn("complete time out {}", url);
                    gatewayContext.setThrowable(new ResponseException(ResponseCode.REQUEST_TIMEOUT));
                } else {
                    gatewayContext.setThrowable(new ConnectException(throwable, gatewayContext.getUniqueId(), url, ResponseCode.HTTP_RESPONSE_ERROR));
                }
            } else {
                gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(response));
            }
        } catch (Throwable e) {
            gatewayContext.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
            log.error("complete error", e);
        } finally {
            gatewayContext.writtened();
            ResponseHelper.writeResponse(gatewayContext);
        }
    }
}
