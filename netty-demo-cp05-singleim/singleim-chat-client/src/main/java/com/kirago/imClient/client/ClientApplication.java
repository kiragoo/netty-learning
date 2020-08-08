package com.kirago.imClient.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
* @Description:    客户端启动程序
* @Author:         kirago
* @CreateDate:     2020/8/7 10:18 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
@Configuration
@ComponentScan("com.kirago.imClient")
@SpringBootApplication
public class ClientApplication {
    /**
     * @param args
     */
    public static void main(String[] args) {
        // 启动并初始化 Spring 环境及其各 Spring 组件
        ApplicationContext context =
                SpringApplication.run(ClientApplication.class, args);
        CommandController commandClient =
                context.getBean(CommandController.class);

        // 初始化客户端控制台
        commandClient.initCommandMap();
        try {
            commandClient.startCommandThread();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
