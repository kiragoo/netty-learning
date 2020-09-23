package com.kirago.netty.im.client;

import com.kirago.netty.im.client.feignClient.UserService;
import com.kirago.netty.im.common.entity.DTO.UserDTO;
import com.kirago.netty.im.common.entity.PT.UserPT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(classes = ImClientApplication.class)
@WebAppConfiguration
class ImClientApplicationTests {
    
    private static void beforeLog(){
        log.info("[测试开始] ------>");
    }

    private static void afterLog(){
        log.info("[测试结束] ------>");
    }
    
    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
    }
    
    
    
    @Test
    public void testUserService(){
        beforeLog();
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        userDTO.setPassword("test");
        UserPT userPT = userService.login(userDTO);
        
        log.info("用户[{}]登录", userDTO.getUsername());
        afterLog();
    }

}
