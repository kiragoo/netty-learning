package com.kirago.netty.im.client;

import com.kirago.netty.im.client.client.CommandController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

@EnableFeignClients
@SpringBootApplication
public class ImClientApplication {

    public static void main(String[] args) {
        ApplicationContext context =  SpringApplication.run(ImClientApplication.class, args);

        CommandController commandClient =
                context.getBean(CommandController.class);

        commandClient.initCommandMap();
        try {
            commandClient.startCommandThread();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
