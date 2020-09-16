package com.kirago.netty.im.client.protoBuilder;


import com.kirago.netty.im.client.client.ClientSession;
import com.kirago.netty.im.common.entity.DTO.UserDTO;
import com.kirago.netty.im.common.entity.PT.ChatMsg;
import com.kirago.netty.im.common.entity.PT.UserPT;
import com.kirago.netty.im.common.protocol.Proto3Msg;

/**
 * 聊天消息Builder
 */

public class ChatMsgBuilder extends BaseBuilder {


    private ChatMsg chatMsg;
    private UserPT userPT;


    public ChatMsgBuilder(ChatMsg chatMsg, UserPT userPT, ClientSession session) {
        super(Proto3Msg.ProtoMsg.HeadType.MESSAGE_REQUEST, session);
        this.chatMsg = chatMsg;
        this.userPT = userPT;

    }


    public Proto3Msg.ProtoMsg.Message build() {
        Proto3Msg.ProtoMsg.Message message = buildCommon(-1);
        Proto3Msg.ProtoMsg.MessageRequest.Builder cb
                = Proto3Msg.ProtoMsg.MessageRequest.newBuilder();

        chatMsg.fillMsg(cb);
        return message
                .toBuilder()
                .setMessageRequest(cb)
                .build();
    }

    public static Proto3Msg.ProtoMsg.Message buildChatMsg(
            ChatMsg chatMsg,
            UserPT userPT,
            ClientSession session) {
        ChatMsgBuilder builder =
                new ChatMsgBuilder(chatMsg, userPT, session);
        return builder.build();

    }
}