package org.imooc.core.request;


import org.asynchttpclient.Request;
import org.asynchttpclient.cookie.Cookie;

/**
 * @author maruimin
 * @PROJECT_NAME: api-gateway-prepare
 * @DESCRIPTION: 提供可修改的Request参数操作接口
 * @USER: WuYang
 * @DATE: 2022/12/24 21:00
 * 接下来我们就开始写我们网关自己的核心上下文，同样我们对外提供一个接口，
 * 便于后续同学们根据自己的业务，自己去适配
 */
public interface IGatewayRequest {

    /**
     * 第一个我能想到的是目标服务的地址，我们需要修改，为什么呢
     * 因为我们一开始拿到的是一个后端服务的域名，这时候我们就需要通过注册中心，
     * 在通过自己的负载均衡算法，拿到真正服务的IP地址，然后替换掉其中的host，我们就先定义一些这个接口
     *
     * @param host
     */
    void setModifyHost(String host);

    /**
     * 有修改就会有获取，我们写一个获取方法
     *
     * @return
     */
    String getModifyHost();

    /**
     * 有了目标地址，我们还需要组装最后的请求，我们同样需要这样一个方法
     *
     * @param path
     */
    void setModifyPath(String path);

    /**
     * 同样我们需要获取路径path
     *
     * @return
     */
    String getModifyPath();

    /**
     * 精通网络请求或者协议的同学，我们还知道，我们还需要添加请求头
     * 添加请求头信息
     *
     * @return
     */
    void addHeader(CharSequence name, String value);

    /**
     * 以及设置请求头
     *
     * @return
     */
    void setHeader(CharSequence name, String value);

    /**
     * 那么还有什么我们需要注意的呢
     * 是不是它本身的请求参数
     *
     * @return
     */
    void addQueryParam(String name, String value);

    /**
     * 有些参数是表单模式的，此时我们也需要提供该种类型的支持
     *
     * @return
     */
    void addFormParam(String name, String value);

    /**
     * 那么请求下游服务，怎么认定你这个服务是安全的呢，是不是我们还需要替换Cookie
     *
     * @return
     */
    void addOrReplaceCookie(Cookie cookie);

    /**
     * 大家回顾一下，还有什么需要我们进行设置呢，大家有没有发现，超时时间也是很重要的一个属性，这时我们需要设置
     *
     * @return
     */
    void setRequestTimeout(int requestTimeout);

    /**
     * 各种参数都组装好了以后，我们是不是需要知道最终的请求路径呢，我们提供这样一个方法
     *
     * @return
     */
    String getFinalUrl();

    /**
     * 最终我们
     * 构建转发请求的请求对象
     *
     * @return
     */
    Request build();

}
