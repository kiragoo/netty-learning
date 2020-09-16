package com.kirago.netty.im.client.ClientSender;


import com.kirago.netty.im.client.protoBuilder.ChatMsgBuilder;
import com.kirago.netty.im.common.entity.PT.ChatMsg;
import com.kirago.netty.im.common.protocol.Proto3Msg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("ChatSender")
public class ChatSender extends BaseSender {

    public void sendChatMsg(String toUserId,String content) {
        ChatMsg chatMsg = new ChatMsg(getUserPT());
        chatMsg.setContent(content);
        chatMsg.setMsgType(ChatMsg.MSGTYPE.TEXT);
        chatMsg.setTo(toUserId);
        chatMsg.setMsgId(System.currentTimeMillis());
        Proto3Msg.ProtoMsg.Message message =
                ChatMsgBuilder.buildChatMsg(chatMsg, getUserPT(), getSession());
//        commandClient.waitCommandThread();
        super.sendMsg(message);
    }

    @Override
    protected void sendSucced(Proto3Msg.ProtoMsg.Message message) {



        log.info("单聊发送成功:"
                + message.getMessageRequest().getContent()
                + "->"
                + message.getMessageRequest().getTo());
//        commandClient.notifyCommandThread();
    }

    @Override
    protected void sendException(Proto3Msg.ProtoMsg.Message message) {
        log.info("单聊发送异常:"
                + message.getMessageRequest().getContent()
                + "->"
                + message.getMessageRequest().getTo());
//        commandClient.notifyCommandThread();
    }

    @Override
    protected void sendFailed(Proto3Msg.ProtoMsg.Message message) {
        log.info("单聊发送失败:"
                + message.getMessageRequest().getContent()
                + "->"
                + message.getMessageRequest().getTo());
//        commandClient.notifyCommandThread();
    }
}
