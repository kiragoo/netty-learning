package com.kirago.netty.im.client.protoBuilder;


import com.kirago.netty.im.client.client.ClientSession;
import com.kirago.netty.im.common.entity.PT.UserPT;
import com.kirago.netty.im.common.protocol.Proto3Msg;

/**
 * 心跳消息Builder
 */
public class HeartBeatMsgBuilder extends BaseBuilder {
    private final UserPT userPT;

    public HeartBeatMsgBuilder(UserPT userPT, ClientSession session) {
        super(Proto3Msg.ProtoMsg.HeadType.HEART_BEAT, session);
        this.userPT = userPT;
    }

    public Proto3Msg.ProtoMsg.Message buildMsg() {
        Proto3Msg.ProtoMsg.Message message = buildCommon(-1);
        Proto3Msg.ProtoMsg.MessageHeartBeat.Builder lb =
                Proto3Msg.ProtoMsg.MessageHeartBeat.newBuilder()
                        .setSeq(0)
                        .setJson("{\"from\":\"client\"}")
                        .setUid(userPT.getUserId());
        return message.toBuilder().setHeartBeat(lb).build();
    }


}


