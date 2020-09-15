package com.kirago.netty.im.server;

import com.kirago.netty.im.server.chatServer.ChatServer;
import com.kirago.netty.im.server.session.SessionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ImServerApplication {

    public static void main(String[] args) {
        ApplicationContext context =  SpringApplication.run(ImServerApplication.class, args);

        SessionManager sessionManager = context.getBean(SessionManager.class);
        SessionManager.setSingleInstance(sessionManager);

        ChatServer nettyServer = context.getBean(ChatServer.class);
        nettyServer.run();
    }

}
