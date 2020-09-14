package com.kirago.netty.im.server.processor;

import com.kirago.netty.im.common.protocol.Proto3Msg;
import com.kirago.netty.im.server.session.LocalSession;

/**
* @description:    操作类
* @author:         kirago
* @date:     2020/9/14 6:05 下午
* @updateRemark:   修改内容
* @version:        1.0
*/
public interface ServerProcessor {

    Proto3Msg.ProtoMsg.HeadType type();

    boolean action(LocalSession ch, Proto3Msg.ProtoMsg.Message proto);
}
