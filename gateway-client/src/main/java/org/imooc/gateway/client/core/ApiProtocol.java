package org.imooc.gateway.client.core;

/**
 * @author maruimin
 * @date 2023/5/31 20:59
 */
public enum ApiProtocol {

    /**
     * api协议枚举
     */
    HTTP("http", "http协议"),
    DUBBO("dubbo", "dubbo协议"),
    ;

    private final String code;

    private final String desc;

    ApiProtocol(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
