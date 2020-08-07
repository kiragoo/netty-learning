package com.kirago.imServer.protoBuilder;

import com.kirago.imCommon.common.ProtoInstant;
import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import org.springframework.stereotype.Service;

@Service("LoginResponceBuilder")
public class LoginResponseBuilder {

    /**
     * 登录应答 应答消息protobuf
     */
    public ProtoMsg3.Message loginResponce(
            ProtoInstant.ResultCodeEnum en, long seqId, String sessionId) {
        ProtoMsg3.Message.Builder mb = ProtoMsg3.Message.newBuilder()
                .setType(ProtoMsg3.HeadType.LOGIN_RESPONSE)  //设置消息类型
                .setSequence(seqId)
                .setSessionId(sessionId);  //设置应答流水，与请求对应

        ProtoMsg3.LoginResponse.Builder b = ProtoMsg3.LoginResponse.newBuilder()
                .setCode(en.getCode())
                .setInfo(en.getDesc())
                .setExpose(1);

        mb.setLoginResponse(b.build());
        return mb.build();
    }


}

