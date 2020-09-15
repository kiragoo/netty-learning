package com.kirago.netty.im.server.processor;

import com.kirago.netty.im.common.protocol.Proto3Msg;
import com.kirago.netty.im.server.session.LocalSession;
import com.kirago.netty.im.server.session.ServerSession;
import com.kirago.netty.im.server.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class ChatRedirectProcessor extends AbstractServerProcessor {
    
    @Override
    public Proto3Msg.ProtoMsg.HeadType type() {
        return Proto3Msg.ProtoMsg.HeadType.MESSAGE_REQUEST;
    }

    @Override
    public Boolean action(LocalSession fromSession, Proto3Msg.ProtoMsg.Message proto) {
        // 聊天处理
        Proto3Msg.ProtoMsg.MessageRequest msg = proto.getMessageRequest();
        log.info("chatMsg | from="
                + msg.getFrom()
                + " , to=" + msg.getTo()
                + " , content=" + msg.getContent());
        // 获取接收方的chatID
        String to = msg.getTo();
        // int platform = msg.getPlatform();
        List<ServerSession> toSessions = SessionManager.inst().getSessionsBy(to);
        if (toSessions == null) {
            //接收方离线
            log.info("[" + to + "] 不在线，发送失败!");
        } else {

            toSessions.forEach((session)->{
                // 将IM消息发送到接收方
                session.writeAndFlush(proto);
            });
        }
        return null;
    }

}
