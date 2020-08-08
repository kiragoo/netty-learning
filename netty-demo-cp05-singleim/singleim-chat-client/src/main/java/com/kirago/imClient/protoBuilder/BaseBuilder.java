package com.kirago.imClient.protoBuilder;

import com.kirago.imClient.client.ClientSession;
import com.kirago.imCommon.common.bean.msg.ProtoMsg3;

/**
* @Description:    protocol 基类
* @Author:         kirago
* @CreateDate:     2020/8/8 10:14 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class BaseBuilder {
    
    protected ProtoMsg3.HeadType type;

    private ClientSession session;

    public BaseBuilder(ProtoMsg3.HeadType type, ClientSession session) {
        this.type = type;
        this.session = session;
    }

    /**
    * @Description: 构建消息，基础部分
    * @Param: 
    * @return: 
    **/
    public ProtoMsg3.Message buildCommon(long seqId) {

        ProtoMsg3.Message.Builder mb =
                ProtoMsg3.Message
                        .newBuilder()
                        .setType(type)
                        .setSessionId(session.getSessionId())
                        .setSequence(seqId);
        return mb.buildPartial();
    }
}
