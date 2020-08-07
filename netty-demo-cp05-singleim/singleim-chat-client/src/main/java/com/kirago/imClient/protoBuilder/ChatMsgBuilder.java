package com.kirago.imClient.protoBuilder;


import com.kirago.imClient.client.ClientSession;
import com.kirago.imCommon.common.bean.ChatMsg;
import com.kirago.imCommon.common.bean.User;
import com.kirago.imCommon.common.bean.msg.ProtoMsg3;

public class ChatMsgBuilder extends BaseBuilder {


    private ChatMsg chatMsg;
    private User user;


    public ChatMsgBuilder(ChatMsg chatMsg, User user, ClientSession session) {
        super(ProtoMsg3.HeadType.MESSAGE_REQUEST, session);
        this.chatMsg = chatMsg;
        this.user = user;

    }


    public ProtoMsg3.Message build() {
        ProtoMsg3.Message message = buildCommon(-1);
        ProtoMsg3.MessageRequest.Builder cb
                = ProtoMsg3.MessageRequest.newBuilder();

        chatMsg.fillMsg(cb);
        return message
                .toBuilder()
                .setMessageRequest(cb)
                .build();
    }

    public static ProtoMsg3.Message buildChatMsg(
            ChatMsg chatMsg,
            User user,
            ClientSession session) {
        ChatMsgBuilder builder =
                new ChatMsgBuilder(chatMsg, user, session);
        return builder.build();

    }
}

