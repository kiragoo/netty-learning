package com.kirago.imServer.protoBuilder;

import com.kirago.imCommon.common.ProtoInstant;
import com.kirago.imCommon.common.bean.msg.ProtoMsg3;

public class ChatMsgBuilder {
    public static ProtoMsg3.Message buildChatResponse(
            long seqId,
            ProtoInstant.ResultCodeEnum en) {
        ProtoMsg3.Message.Builder mb = ProtoMsg3.Message.newBuilder()
                .setType(ProtoMsg3.HeadType.MESSAGE_RESPONSE)  //设置消息类型
                .setSequence(seqId);                 //设置应答流水，与请求对应
        ProtoMsg3.MessageResponse.Builder rb =
                ProtoMsg3.MessageResponse.newBuilder()
                        .setCode(en.getCode())
                        .setInfo(en.getDesc())
                        .setExpose(1);
        mb.setMessageResponse(rb.build());
        return mb.build();
    }


    /**
     * 登录应答 应答消息protobuf
     */
    public static ProtoMsg3.Message buildLoginResponce(
            ProtoInstant.ResultCodeEnum en,
            long seqId) {
        ProtoMsg3.Message.Builder mb = ProtoMsg3.Message.newBuilder()
                .setType(ProtoMsg3.HeadType.MESSAGE_RESPONSE)  //设置消息类型
                .setSequence(seqId);  //设置应答流水，与请求对应

        ProtoMsg3.LoginResponse.Builder rb =
                ProtoMsg3.LoginResponse.newBuilder()
                        .setCode(en.getCode())
                        .setInfo(en.getDesc())
                        .setExpose(1);

        mb.setLoginResponse(rb.build());
        return mb.build();
    }
}
