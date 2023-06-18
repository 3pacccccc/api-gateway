package org.imooc.backend.http.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author maruimin
 * @date 2023/5/19 23:07
 */

@SpringBootApplication(scanBasePackages = "org.imooc.backend")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
