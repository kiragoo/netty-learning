package com.kirago.imClient.protoBuilder;


import com.kirago.imClient.client.ClientSession;
import com.kirago.imCommon.common.bean.User;
import com.kirago.imCommon.common.bean.msg.ProtoMsg3;

public class HeartBeatMsgBuilder extends BaseBuilder{
    
    private User user;
    
    public HeartBeatMsgBuilder(User user, ClientSession session){
        super(ProtoMsg3.HeadType.HEART_BEAT, session);
        this.user = user;
    }
    
    public ProtoMsg3.Message buildMsg(){
        ProtoMsg3.Message message = buildCommon(-1);
        ProtoMsg3.MessageHeartBeat.Builder lb =
                ProtoMsg3.MessageHeartBeat.newBuilder()
                        .setSeq(0)
                        .setJson("{\"from\":\"client\"}")
                        .setUid(user.getUid());
        return message.toBuilder().setHeartBeat(lb).build();
    }
}
