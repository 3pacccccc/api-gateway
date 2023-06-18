package org.imooc.core;

import lombok.extern.slf4j.Slf4j;
import org.imooc.common.utils.PropertiesUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @author maruimin
 * @date 2023/5/11 22:57
 */

@Slf4j
public class ConfigLoader {

    private static final String CONFIG_FILE = "gateway.properties";
    private static final String ENV_PREFIX = "GATEWAY_";
    private static final String JVM_PREFIX = "gateway.";

    private static final ConfigLoader INSTANCE = new ConfigLoader();

    private ConfigLoader() {
    }

    public static ConfigLoader getInstance() {
        return INSTANCE;
    }

    private Config config;

    public static Config getConfig() {
        return INSTANCE.config;
    }

    /**
     * 优先级高的会覆盖优先级低的
     * 运行参数 -> jvm参数 -> 环境变量 -> 配置文件 -> 配置对象默认值
     *
     * @param args
     * @return
     */
    public Config load(String[] args) {
        // 配置对象对默认值
        config = new Config();
        //配置文件
        loadFromConfigFile();
        //环境变量
        loadFromEnv();
        //jvm参数
        loadFromJvm();
        //运行参数
        loadFromArgs(args);
        return config;

    }

    private void loadFromArgs(String[] args) {
        // program arguments --port=4444
        if (args != null && args.length > 0) {
            Properties properties = new Properties();
            for (String arg : args) {
                if (arg.startsWith("--") && arg.contains("=")) {
                    properties.put(arg.substring(2, arg.indexOf("=")),
                            arg.substring(arg.indexOf("=") + 1));
                }
            }
            PropertiesUtils.properties2Object(properties, config);
        }
    }

    private void loadFromJvm() {
        // vm options -Dgateway.port=3333
        Properties properties = System.getProperties();
        PropertiesUtils.properties2Object(properties, config, JVM_PREFIX);
    }

    private void loadFromEnv() {
        // 环境变量设置GATEWAY_port=2222
        Map<String, String> env = System.getenv();
        Properties properties = new Properties();
        properties.putAll(env);
        PropertiesUtils.properties2Object(properties, config, ENV_PREFIX);
    }

    private void loadFromConfigFile() {
        // 配置文件gateway.properties里面配置port=1111
        InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (inputStream != null) {
            Properties properties = new Properties();
            try {
                properties.load(inputStream);
                PropertiesUtils.properties2Object(properties, config);
            } catch (IOException e) {
                log.warn("load config file {} error, e:{}", CONFIG_FILE, e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
    }

}
