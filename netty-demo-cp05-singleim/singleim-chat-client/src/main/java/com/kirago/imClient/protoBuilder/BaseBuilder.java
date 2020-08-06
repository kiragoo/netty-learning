package com.kirago.imClient.protoBuilder;

import com.kirago.imClient.client.ClientSession;
import com.kirago.imCommon.common.bean.msg.ProtoMsg3;

public class BaseBuilder {
    
    protected ProtoMsg3.HeadType type;
    
    private long seqId;
    
    private ClientSession session;

    public BaseBuilder(ProtoMsg3.HeadType type, ClientSession session) {
        this.type = type;
        this.session = session;
    }

    /**
     * 构建消息 基础部分
     */
    public ProtoMsg3.Message buildCommon(long seqId) {
        this.seqId = seqId;

        ProtoMsg3.Message.Builder mb =
                ProtoMsg3.Message
                        .newBuilder()
                        .setType(type)
                        .setSessionId(session.getSessionId())
                        .setSequence(seqId);
        return mb.buildPartial();
    }
}
