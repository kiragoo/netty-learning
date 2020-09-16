package com.kirago.netty.im.client.protoBuilder;


import com.kirago.netty.im.client.client.ClientSession;
import com.kirago.netty.im.common.protocol.Proto3Msg;

/**
 * 基础 Builder
 *
 * @author 尼恩 at  疯狂创客圈
 */
public class BaseBuilder {
    protected Proto3Msg.ProtoMsg.HeadType type;
    private long seqId;
    private ClientSession session;

    public BaseBuilder(Proto3Msg.ProtoMsg.HeadType type, ClientSession session) {
        this.type = type;
        this.session = session;
    }

    /**
     * 构建消息 基础部分
     */
    public Proto3Msg.ProtoMsg.Message buildCommon(long seqId) {
        this.seqId = seqId;

        Proto3Msg.ProtoMsg.Message.Builder mb =
                Proto3Msg.ProtoMsg.Message
                        .newBuilder()
                        .setType(type)
                        .setSessionId(session.getSessionId())
                        .setSequence(seqId);
        return mb.buildPartial();
    }

}
