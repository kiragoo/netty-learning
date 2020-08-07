package com.kirago.imServer.processor;

import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import com.kirago.imCommon.utils.PrintUitl;
import com.kirago.imServer.server.ServerSession;
import com.kirago.imServer.server.SessionMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("ChatRedirectProcesser")
public class ChatRedirectProcessor extends AbstractServerProcessor {
    
    @Override
    public ProtoMsg3.HeadType type() {
        return ProtoMsg3.HeadType.MESSAGE_REQUEST;
    }

    @Override
    public boolean action(ServerSession fromSession, ProtoMsg3.Message proto) {
        // 聊天处理
        ProtoMsg3.MessageRequest msg = proto.getMessageRequest();
        PrintUitl.tcfo("chatMsg | from="
                + msg.getFrom()
                + " , to=" + msg.getTo()
                + " , content=" + msg.getContent());
        // 获取接收方的chatID
        String to = msg.getTo();
        // int platform = msg.getPlatform();
        List<ServerSession> toSessions = SessionMap.inst().getSessionsBy(to);
        if (toSessions == null) {
            //接收方离线
            PrintUitl.tcfo("[" + to + "] 不在线，发送失败!");
        } else {

            toSessions.forEach((session) -> {
                // 将IM消息发送到接收方
                session.writeAndFlush(proto);
            });
        }
        return true;
    }

}

