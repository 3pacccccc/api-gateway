package org.imooc.gateway.client.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author maruimin
 * @date 2023/6/1 22:34
 */

@Data
@ConfigurationProperties(prefix = "api")
public class ApiProperties {

    private String registerAddress;

    private String env = "dev";
}
