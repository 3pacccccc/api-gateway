package org.imooc.core.filter.router;

import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.imooc.common.constants.FilterConst;
import org.imooc.common.enums.ResponseCode;
import org.imooc.common.exception.ConnectException;
import org.imooc.common.exception.ResponseException;
import org.imooc.common.rule.Rule;
import org.imooc.core.ConfigLoader;
import org.imooc.core.context.GatewayContext;
import org.imooc.core.filter.Filter;
import org.imooc.core.filter.FilterAspect;
import org.imooc.core.helper.AsyncHttpHelper;
import org.imooc.core.helper.ResponseHelper;
import org.imooc.core.response.GatewayResponse;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

/**
 * @author maruimin
 * @date 2023/7/9 10:26
 */

@Slf4j
@FilterAspect(id = FilterConst.ROUTER_FILTER_ID,
        name = FilterConst.ROUTER_FILTER_NAME,
        order = FilterConst.ROUTER_FILTER_ORDER
)
public class RouterFilter implements Filter {
    /**
     * 过滤
     *
     * @param ctx 网关上下文
     * @throws Exception 异常
     */
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        Request request = ctx.getRequest().build();
        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().executeRequest(request);
        boolean whenComplete = ConfigLoader.getConfig().isWhenComplete();
        if (whenComplete) {
            future.whenComplete((response, throwable) -> {
                complete(request, response, throwable, ctx);
            });
        } else {
            future.whenCompleteAsync((response, throwable) -> {
                complete(request, response, throwable, ctx);
            });
        }
    }

    private void complete(Request request,
                          Response response,
                          Throwable throwable,
                          GatewayContext gatewayContext) {
        gatewayContext.releaseRequest();
//        Rule rule = gatewayContext.getRule();
//        int currentRetryTimes = gatewayContext.getCurrentRetryTimes();
//        int confRetryTimes = rule.getRetryConfig().getTimes();
//
//        boolean retry = (throwable instanceof TimeoutException || throwable instanceof IOException) && currentRetryTimes <= confRetryTimes;
//        if (retry) {
//            // 进行重试
//            doRetry(gatewayContext, currentRetryTimes);
//        }
        try {
            if (Objects.nonNull(throwable)) {
                String url = request.getUrl();
                if (throwable instanceof TimeoutException) {
                    log.warn("complete time out {}", url);
                    gatewayContext.setThrowable(new ResponseException(ResponseCode.REQUEST_TIMEOUT));
                    gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.REQUEST_TIMEOUT));
                } else {
                    gatewayContext.setThrowable(new ConnectException(throwable, gatewayContext.getUniqueId(), url, ResponseCode.HTTP_RESPONSE_ERROR));
                    gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.HTTP_RESPONSE_ERROR));
                }
            } else {
                gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(response));
            }
        } catch (Throwable t) {
            gatewayContext.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
            gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.INTERNAL_ERROR));
        } finally {
            gatewayContext.writtened();
            ResponseHelper.writeResponse(gatewayContext);
        }
    }

    private void doRetry(GatewayContext gatewayContext, int currentRetryTimes) {
        gatewayContext.setCurrentRetryTimes(currentRetryTimes + 1);
        try {
            doFilter(gatewayContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
