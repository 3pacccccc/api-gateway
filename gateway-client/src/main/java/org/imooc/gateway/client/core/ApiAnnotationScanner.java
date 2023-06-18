package org.imooc.gateway.client.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.spring.ServiceBean;
import org.imooc.common.config.DubboServiceInvoker;
import org.imooc.common.config.HttpServiceInvoker;
import org.imooc.common.config.ServiceDefinition;
import org.imooc.common.config.ServiceInvoker;
import org.imooc.common.constants.BasicConst;
import org.imooc.gateway.client.support.dubbo.DubboConstants;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

/**
 * 注解扫描类
 *
 * @author maruimin
 * @date 2023/5/31 21:17
 */
public class ApiAnnotationScanner {

    private ApiAnnotationScanner() {
    }

    private static class SingletonHolder {
        static final ApiAnnotationScanner INSTANCE = new ApiAnnotationScanner();
    }

    public static ApiAnnotationScanner getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private HttpServiceInvoker createHttpServiceInvoker(String path) {
        HttpServiceInvoker httpServiceInvoker = new HttpServiceInvoker();
        httpServiceInvoker.setInvokerPath(path);
        return httpServiceInvoker;
    }

    private DubboServiceInvoker createDubboServiceInvoker(String path, ServiceBean<?> serviceBean, Method method) {
        DubboServiceInvoker dubboServiceInvoker = new DubboServiceInvoker();
        dubboServiceInvoker.setInvokerPath(path);

        String methodName = method.getName();
        String registerAddress = serviceBean.getRegistry().getAddress();
        String interfaceClass = serviceBean.getInterface();

        dubboServiceInvoker.setRegisterAddress(registerAddress);
        dubboServiceInvoker.setMethodName(methodName);
        dubboServiceInvoker.setInterfaceClass(interfaceClass);

        String[] parameterTypes = new String[method.getParameterCount()];
        Parameter[] classes = method.getParameters();
        for (int i = 0; i < classes.length; i++) {
            parameterTypes[i] = classes[i].getName();
        }
        dubboServiceInvoker.setParameterTypes(parameterTypes);

        Integer serviceTimeout = serviceBean.getTimeout();
        if (serviceTimeout == null || serviceTimeout.intValue() == 0) {
            ProviderConfig providerConfig = serviceBean.getProvider();
            if (providerConfig != null) {
                Integer providerTimeout = providerConfig.getTimeout();
                if (providerTimeout == null || providerTimeout.intValue() == 0) {
                    serviceTimeout = DubboConstants.DUBBO_TIMEOUT;
                } else {
                    serviceTimeout = providerTimeout;
                }
            }
        }

        dubboServiceInvoker.setTimeout(serviceTimeout);

        String dubboVersion = serviceBean.getVersion();
        dubboServiceInvoker.setVersion(dubboVersion);
        return dubboServiceInvoker;

    }

    /**
     * 扫描传入的bean对象，最终返回一个服务定义
     * @param bean 待扫描的对象
     * @param args 参数
     * @return 服务定义
     */
    public ServiceDefinition scanner(Object bean, Object... args) {

        Class<?> aClass = bean.getClass();
        if (!aClass.isAnnotationPresent(ApiService.class)) {
            return null;
        }

        ApiService apiService = aClass.getAnnotation(ApiService.class);
        String serviceId = apiService.serviceId();
        ApiProtocol protocol = apiService.protocol();
        String patternPath = apiService.patterPath();
        String version = apiService.version();

        ServiceDefinition serviceDefinition = new ServiceDefinition();
        HashMap<String, ServiceInvoker> invokerMap = new HashMap<>(16);
        Method[] methods = aClass.getMethods();
        if (methods.length > 0) {
            for (Method method : methods) {
                ApiInvoker apiInvoker = method.getAnnotation(ApiInvoker.class);
                if (apiInvoker == null) {
                    continue;
                }

                String path = apiInvoker.path();
                switch (protocol) {
                    case HTTP:
                        HttpServiceInvoker httpServiceInvoker = createHttpServiceInvoker(path);
                        invokerMap.put(path, httpServiceInvoker);
                        break;
                    case DUBBO:
                        ServiceBean<?> serviceBean = (ServiceBean<?>) args[0];
                        DubboServiceInvoker dubboServiceInvoker = createDubboServiceInvoker(path, serviceBean, method);
                        String dubboVersion = dubboServiceInvoker.getVersion();
                        if (StringUtils.isNotBlank(dubboVersion)) {
                            version = dubboVersion;
                        }
                        invokerMap.put(path, dubboServiceInvoker);
                        break;
                    default:
                        break;
                }

            }

            serviceDefinition.setUniqueId(serviceId + BasicConst.COLON_SEPARATOR + version);
            serviceDefinition.setServiceId(serviceId);
            serviceDefinition.setVersion(version);
            serviceDefinition.setProtocol(protocol.getCode());
            serviceDefinition.setPatternPath(patternPath);
            serviceDefinition.setEnable(true);
            serviceDefinition.setInvokerMap(invokerMap);
            return serviceDefinition;
        }
        return null;
    }

}
