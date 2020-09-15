package com.kirago.netty.im.client.protoBuilder;

import com.kirago.netty.im.client.client.ClientSession;
import com.kirago.netty.im.common.entity.DTO.UserDTO;
import com.kirago.netty.im.common.protocol.Proto3Msg;

/**
 * 登陆消息Builder
 */
public class LoginMsgBuilder extends BaseBuilder {
    private final UserDTO user;

    public LoginMsgBuilder(UserDTO user, ClientSession session) {
        super(Proto3Msg.ProtoMsg.HeadType.LOGIN_REQUEST, session);
        this.user = user;
    }

    public Proto3Msg.ProtoMsg.Message build() {
        Proto3Msg.ProtoMsg.Message message = buildCommon(-1);
        Proto3Msg.ProtoMsg.LoginRequest.Builder lb =
                Proto3Msg.ProtoMsg.LoginRequest.newBuilder()
                        .setDeviceId(user.getDevId())
                        .setPlatform(user.getPlatform().ordinal())
                        .setToken(user.getToken())
                        .setUid(user.getUserId());
        return message.toBuilder().setLoginRequest(lb).build();
    }

    public static ProtoMsg.Message buildLoginMsg(
            UserDTO user, ClientSession session) {
        LoginMsgBuilder builder =
                new LoginMsgBuilder(user, session);
        return builder.build();

    }
}


