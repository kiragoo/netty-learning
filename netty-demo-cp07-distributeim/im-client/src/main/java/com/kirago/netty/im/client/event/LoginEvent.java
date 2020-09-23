package com.kirago.netty.im.client.event;

import com.kirago.netty.im.common.entity.DTO.UserDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Data
@Service
public class LoginEvent implements BaseEvent<UserDTO>{
    public static final String KEY = "1";


    @Override
    public  void exec(UserDTO userDTO){
        log.info("登录用户为: [{}]", userDTO.getUsername());
    }
    
    @Override
    public String getKey(){
        return KEY;
    }
    
    @Override
    public String getTip(){
        return "登录";
    }
}
