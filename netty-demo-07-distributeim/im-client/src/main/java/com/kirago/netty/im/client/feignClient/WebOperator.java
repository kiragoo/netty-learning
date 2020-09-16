package com.kirago.netty.im.client.feignClient;

import com.kirago.netty.im.common.balance.ImLoadBalance;
import com.kirago.netty.im.common.constants.ServerConstants;
import com.kirago.netty.im.common.entity.DTO.UserDTO;
import com.kirago.netty.im.common.entity.PT.ImNode;
import com.kirago.netty.im.common.entity.PT.LoginBack;
import com.kirago.netty.im.common.entity.PT.UserPT;
import feign.Feign;
import feign.codec.StringDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebOperator {
    
    @Autowired
    private static ImLoadBalance imLoadBalance;
    
    public static LoginBack login(UserDTO userDTO) {
        UserService userService = Feign.builder()
//                .decoder(new GsonDecoder())
                .decoder(new StringDecoder())
                .target(UserService.class, ServerConstants.WEB_URL);
        

//        String s = userService.login(userDTO);
        UserPT userPT = userService.login(userDTO);

        LoginBack loginBack = new LoginBack();
        loginBack.setUserPT(userPT);
        ImNode bestNode = imLoadBalance.getBestWorker();
        loginBack.setImNode(bestNode);
        loginBack.setToken(userPT.getToken());

        return loginBack;

    }
}
