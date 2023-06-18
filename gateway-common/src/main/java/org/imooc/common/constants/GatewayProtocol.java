package org.imooc.common.constants;

/**
 * @author maruimin
 * @date 2023/5/19 20:41
 */
public interface GatewayProtocol {
	
	String HTTP = "http";
	
	String DUBBO = "dubbo";
	
	static boolean isHttp(String protocol) {
		return HTTP.equals(protocol);
	}
	
	static boolean isDubbo(String protocol) {
		return DUBBO.equals(protocol);
	}
	
}
