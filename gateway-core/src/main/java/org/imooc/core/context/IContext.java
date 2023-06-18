package org.imooc.core.context;

import io.netty.channel.ChannelHandlerContext;
import org.imooc.common.rule.Rule;

import java.util.function.Consumer;

/**
 * @author maruimin
 * @date 2023/5/7 21:46
 */
public interface IContext {

    /**
     * 上下文生命周期，定义状态
     * 一个请求正在执行过程中
     */
    int RUNNING = -1;

    /**
     * 中途出现错误，比如运行完过滤器过程中，不一定哪一个出错，这时我们需要进行标记，
     * 告诉我们请求以及结束，需要返回客户端
     */
    int WRITTEN = 0;

    /**
     * 当写回成功后, 设置该标记，如果是Netty的话：ctx.writeAndFlush(response);防止并发下的多次标记写回
     */
    int COMPLETED = 1;

    /**
     * 表示整个网关请求完毕, 彻底结束
     */
    int TERMINATED = 2;


    /**
     * 上下文生命周期，状态流转
     * <B>概要说明：</B>设置上下文状态为正常运行状态<BR>
     */
    void runned();

    /**
     * <B>概要说明：</B>设置上下文状态为标记写回<BR>
     */
    void writtened();

    /**
     * <B>概要说明：</B>设置上下文状态为写回结束<BR>
     */
    void completed();

    /**
     * <B>概要说明：</B>设置上下文状态为最终结束<BR>
     */
    void terminated();

    /*************** -- 判断网关的状态 -- ********************/

    boolean isRunning();

    boolean isWrittened();

    boolean isCompleted();

    boolean isTerminated();


    /**
     * <B>方法名称：</B>getProtocol<BR>
     * <B>概要说明：</B>获取请求转换协议<BR>
     *
     * @return
     */
    String getProtocol();

    /**
     * 获取规则
     *
     * @return
     */
    Rule getRule();

    /**
     * <B>方法名称：</B>getRequest<BR>
     * <B>概要说明：</B>获取请求对象<BR>
     *
     * @return
     */
    Object getRequest();

    /**
     * <B>方法名称：</B>getResponse<BR>
     * <B>概要说明：</B>获取响应对象<BR>
     *
     * @return
     */
    Object getResponse();

    /**
     * <B>方法名称：</B>setResponse<BR>
     * <B>概要说明：</B>设置响应对象<BR>
     *
     * @param response
     */
    void setResponse(Object response);

    /**
     * 设置异常信息
     *
     * @param throwable
     */
    void setThrowable(Throwable throwable);

    /**
     * <B>方法名称：</B>getThrowable<BR>
     * <B>概要说明：</B>获取异常<BR>
     *
     * @return Throwable
     */
    Throwable getThrowable();

    /**
     * 获取上下文参数
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> T getAttribute(String key);

    /**
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    <T> T putAttribute(String key, T value);

    /**
     * 获取Netty上下文
     *
     * @return
     */
    ChannelHandlerContext getNettyCtx();

    /**
     * 是否保持连接
     *
     * @return
     */
    boolean isKeepAlive();

    /**
     * 释放请求资源的方法
     */
    void releaseRequest();

    /**
     * <B>方法名称：</B>completedCallback<BR>
     * <B>概要说明：</B>写回接收回调函数设置<BR>
     *
     * @param consumer
     * @author JiFeng
     * @since 2021年12月9日 上午2:30:02
     */
    void completedCallback(Consumer<IContext> consumer);

    /**
     * <B>方法名称：</B>invokeCompletedCallback<BR>
     * <B>概要说明：</B>回调函数执行<BR>
     *
     * @author JiFeng
     * @since 2021年12月9日 上午2:30:41
     */
    void invokeCompletedCallback();

}
