package com.kirago.imServer.processor;


import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import com.kirago.imServer.server.ServerSession;

/**
 * 操作类
 */
public interface ServerProcessor {

    ProtoMsg3.HeadType type();

    boolean action(ServerSession ch, ProtoMsg3.Message proto);

}

