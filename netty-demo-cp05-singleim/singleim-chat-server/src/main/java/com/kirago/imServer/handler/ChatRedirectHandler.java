package com.kirago.imServer.handler;

import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import com.kirago.imCommon.concurrent.FutureTaskScheduler;
import com.kirago.imServer.processor.ChatRedirectProcessor;
import com.kirago.imServer.server.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("ChatRedirectHandler")
@ChannelHandler.Sharable
public class ChatRedirectHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    ChatRedirectProcessor chatRedirectProcesser;

    /**
     * 收到消息
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        //判断消息实例
        if (null == msg || !(msg instanceof ProtoMsg3.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断消息类型
        ProtoMsg3.Message pkg = (ProtoMsg3.Message) msg;
        ProtoMsg3.HeadType headType = ((ProtoMsg3.Message) msg).getType();
        if (!headType.equals(chatRedirectProcesser.type())) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断是否登录
        ServerSession session = ServerSession.getSession(ctx);
        if (null == session || !session.isLogin()) {
            log.error("用户尚未登录，不能发送消息");
            return;
        }

        //异步处理IM消息转发的逻辑
        FutureTaskScheduler.add(() ->
        {
            chatRedirectProcesser.action(session, pkg);
        });


    }

}


