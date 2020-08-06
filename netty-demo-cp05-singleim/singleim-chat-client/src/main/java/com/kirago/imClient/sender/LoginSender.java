package com.kirago.imClient.sender;

import com.kirago.imClient.protoBuilder.LoginMsgBuilder;
import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("LoginSender")
public class LoginSender extends BaseSender {
    
    public void sendLoginMsg(){
        if(!isConnected()){
            log.info("还未建立连接!");
            return;
        }
        log.info("生成登录消息");
        ProtoMsg3.Message message = LoginMsgBuilder.buildLoginMsg(getUser(), getSession());
        
        log.info("发送登录消息");
        super.sendMsg(message);
    }
}
