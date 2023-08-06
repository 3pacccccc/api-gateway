package org.imooc.core.helper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.imooc.common.config.DynamicConfigManager;
import org.imooc.common.config.HttpServiceInvoker;
import org.imooc.common.config.ServiceDefinition;
import org.imooc.common.constants.BasicConst;
import org.imooc.common.constants.GatewayConst;
import org.imooc.common.enums.ResponseCode;
import org.imooc.common.exception.ResponseException;
import org.imooc.common.rule.Rule;
import org.imooc.core.context.GatewayContext;
import org.imooc.core.request.GatewayRequest;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @author maruimin
 * @date 2023/5/19 20:27
 */
public class RequestHelper {

    public static GatewayContext doContext(FullHttpRequest request, ChannelHandlerContext context) {
        // 构建请求对象GatewayRequest
        GatewayRequest gatewayRequest = doRequest(request, context);
        // 根据请求对象里面的uniqueId，获取资源服务信息(也就是服务定义信息)
        ServiceDefinition serviceDefinition = DynamicConfigManager.getInstance().getServiceDefinition(gatewayRequest.getUniqueId());
        // 根据请求对象获取服务定义对应的方法调用，然后获取对应的规则
        HttpServiceInvoker httpServiceInvoker = new HttpServiceInvoker();
        httpServiceInvoker.setInvokerPath(gatewayRequest.getPath());
        httpServiceInvoker.setTimeout(500);

        // 根据请求对象获取规则
        Rule rule = getRule(gatewayRequest, serviceDefinition.getServiceId());

        // 构建GatewayContext对象
        GatewayContext gatewayContext = new GatewayContext(
                serviceDefinition.getProtocol(),
                context,
                HttpUtil.isKeepAlive(request),
                gatewayRequest,
                rule
        );

        // todo 后续服务发现做完，这儿要改成动态的
//        gatewayContext.getRequest().setModifyHost("127.0.0.1:8080");
        return gatewayContext;
    }

    private static Rule getRule(GatewayRequest gateWayRequest, String serviceId) {
        String key = serviceId + "." + gateWayRequest.getPath();
        Rule rule = DynamicConfigManager.getInstance().getRuleByPath(key);
        if (rule != null) {
            return rule;
        }

        return DynamicConfigManager.getInstance().getRuleByServiceId(serviceId)
                .stream().filter(r -> gateWayRequest.getPath().startsWith(r.getPrefix()))
                .findAny().orElseThrow(() -> new ResponseException(ResponseCode.PATH_NO_MATCHED));
    }

    private static GatewayRequest doRequest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        HttpHeaders headers = fullHttpRequest.headers();
        // 从header头获取必须要传入的关键属性 uniqueId
        String uniqueId = headers.get(GatewayConst.UNIQUE_ID);

        String host = headers.get(HttpHeaderNames.HOST);
        HttpMethod method = fullHttpRequest.method();
        String uri = fullHttpRequest.uri();
        String clientIp = getClientIp(ctx, fullHttpRequest);
        String contentType = HttpUtil.getMimeType(fullHttpRequest) == null ? null : HttpUtil.getMimeType(fullHttpRequest).toString();
        Charset charset = HttpUtil.getCharset(fullHttpRequest, StandardCharsets.UTF_8);
        return new GatewayRequest(
                uniqueId, charset, clientIp, host, uri, method, contentType, headers, fullHttpRequest
        );
    }

    private static String getClientIp(ChannelHandlerContext ctx, FullHttpRequest request) {
        String xForwardedValue = request.headers().get(BasicConst.HTTP_FORWARD_SEPARATOR);

        String clientIp = null;
        if (StringUtils.isNotEmpty(xForwardedValue)) {
            List<String> values = Arrays.asList(xForwardedValue.split(", "));
            if (values.size() >= 1 && StringUtils.isNotBlank(values.get(0))) {
                clientIp = values.get(0);
            }
        }
        if (clientIp == null) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            clientIp = inetSocketAddress.getAddress().getHostAddress();
        }
        return clientIp;
    }
}
