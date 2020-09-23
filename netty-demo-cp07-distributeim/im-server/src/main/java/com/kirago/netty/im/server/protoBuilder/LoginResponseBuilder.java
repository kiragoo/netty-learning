package com.kirago.netty.im.server.protoBuilder;

import com.kirago.netty.im.common.constants.ProtoInstant;
import com.kirago.netty.im.common.protocol.Proto3Msg;
import org.springframework.stereotype.Service;

@Service
public class LoginResponseBuilder {
    /**
     * 登录应答 应答消息protobuf
     */
    public Proto3Msg.ProtoMsg.Message loginResponse(
            ProtoInstant.ResultCodeEnum en, long seqId, String sessionId) {
        Proto3Msg.ProtoMsg.Message.Builder mb = Proto3Msg.ProtoMsg.Message.newBuilder()
                .setType(Proto3Msg.ProtoMsg.HeadType.LOGIN_RESPONSE)  //设置消息类型
                .setSequence(seqId)
                .setSessionId(sessionId);  //设置应答流水，与请求对应

        Proto3Msg.ProtoMsg.LoginResponse.Builder b = Proto3Msg.ProtoMsg.LoginResponse.newBuilder()
                .setCode(en.getCode())
                .setInfo(en.getDesc())
                .setExpose(1);

        mb.setLoginResponse(b.build());
        return mb.build();
    }
}
