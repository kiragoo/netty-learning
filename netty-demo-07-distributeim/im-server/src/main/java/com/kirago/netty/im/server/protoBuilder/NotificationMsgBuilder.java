package com.kirago.netty.im.server.protoBuilder;

import com.kirago.netty.im.common.protocol.Proto3Msg;

public class NotificationMsgBuilder {

    public static  Proto3Msg.ProtoMsg.Message buildNotification(String json) {
        Proto3Msg.ProtoMsg.Message.Builder mb = Proto3Msg.ProtoMsg.Message.newBuilder()
                .setType(Proto3Msg.ProtoMsg.HeadType.MESSAGE_NOTIFICATION) ;   //设置消息类型


        //设置应答流水，与请求对应
        Proto3Msg.ProtoMsg.MessageNotification.Builder rb =
                Proto3Msg.ProtoMsg.MessageNotification.newBuilder()
                        .setJson(json);
        mb.setNotification(rb.build());
        return mb.build();
    }
}
