package com.kirago.netty.im.client;

import com.kirago.netty.im.client.client.EventController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

@EnableFeignClients
@SpringBootApplication
public class ImClientApplication {

    public static void main(String[] args) {
        ApplicationContext context =  SpringApplication.run(ImClientApplication.class, args);

        EventController eventController = context.getBean(EventController.class);
        eventController.initEventMap();
        
        try {
            eventController.startEventThread();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
