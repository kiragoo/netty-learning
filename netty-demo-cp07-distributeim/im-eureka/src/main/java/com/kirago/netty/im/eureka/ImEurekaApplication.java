package com.kirago.netty.im.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ImEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImEurekaApplication.class, args);
    }

}
