package com.kirago.netty.im.client.ClientSender;


import com.kirago.netty.im.client.protoBuilder.LoginMsgBuilder;
import com.kirago.netty.im.common.protocol.Proto3Msg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginSender extends BaseSender {


    public void sendLoginMsg() {
        if (!isConnected()) {
            log.info("还没有建立连接!");
            return;
        }
        log.info("用户[{}]发送登录消息", getUserPT());
        Proto3Msg.ProtoMsg.Message message =
                LoginMsgBuilder.buildLoginMsg(getUserPT(), getSession());
        super.sendMsg(message);
    }


}
