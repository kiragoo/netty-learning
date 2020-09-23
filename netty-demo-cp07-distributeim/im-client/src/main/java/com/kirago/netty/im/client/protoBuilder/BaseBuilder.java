package com.kirago.netty.im.client.protoBuilder;


import com.kirago.netty.im.client.client.ClientSession;
import com.kirago.netty.im.common.protocol.Proto3Msg;

/**
 * 基础 Builder
 *
 */
public class BaseBuilder {
    protected Proto3Msg.ProtoMsg.HeadType type;
    private ClientSession session;

    public BaseBuilder(Proto3Msg.ProtoMsg.HeadType type, ClientSession session) {
        this.type = type;
        this.session = session;
    }

    /**
     * 构建消息 基础部分
     */
    public Proto3Msg.ProtoMsg.Message buildCommon(long seqId) {

        Proto3Msg.ProtoMsg.Message.Builder mb =
                Proto3Msg.ProtoMsg.Message
                        .newBuilder()
                        .setType(type)
                        .setSessionId(session.getSessionId())
                        .setSequence(seqId);
        return mb.buildPartial();
    }

}
