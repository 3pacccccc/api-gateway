package org.imooc.common.config;

/**
 * @author maruimin
 * @date 2023/5/19 20:49
 */
public class AbstractServiceInvoker implements ServiceInvoker{

    protected String invokerPath;

    protected int timeout = 5000;

    /**
     * 获取真正的服务调用的全路径
     */
    @Override
    public String getInvokerPath() {
        return invokerPath;
    }

    @Override
    public void setInvokerPath(String invokerPath) {
        this.invokerPath = invokerPath;
    }

    /**
     * 获取该服务调用(方法)的超时时间
     */
    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
