package com.kirago.netty.im.server.handler;

import com.kirago.netty.im.common.concurrent.FutureTaskScheduler;
import com.kirago.netty.im.common.protocol.Proto3Msg;
import com.kirago.netty.im.server.processor.ChatRedirectProcessor;
import com.kirago.netty.im.server.session.LocalSession;
import com.kirago.netty.im.server.session.ServerSession;
import com.kirago.netty.im.server.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@ChannelHandler.Sharable
public class ChatRedirectHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    ChatRedirectProcessor redirectProcessor;

    @Autowired
    SessionManager sessionManager;

    /**
     * 收到消息
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        //判断消息实例
        if (!(msg instanceof Proto3Msg.ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断消息类型
        Proto3Msg.ProtoMsg.Message pkg = (Proto3Msg.ProtoMsg.Message) msg;
        Proto3Msg.ProtoMsg.HeadType headType = ((Proto3Msg.ProtoMsg.Message) msg).getType();
        if (!headType.equals(redirectProcessor.type())) {
            super.channelRead(ctx, msg);
            return;
        }
        //异步处理转发的逻辑
        FutureTaskScheduler.add(() ->
        {
            //判断是否登录
            LocalSession session = LocalSession.getSession(ctx);
            if (null != session && session.isLogin()) {

                redirectProcessor.action(session, pkg);
                return;
            }

            Proto3Msg.ProtoMsg.MessageRequest request = pkg.getMessageRequest();
            List<ServerSession> toSessions = SessionManager.inst().getSessionsBy(request.getTo());
            final boolean[] isSended = {false};
            toSessions.forEach((serverSession) -> {

                if (serverSession instanceof LocalSession)
                // 将IM消息发送到接收方
                {
                    serverSession.writeAndFlush(pkg);
                    isSended[0] =true;
                }

            });

            if(!isSended[0])
            {
                log.error("用户尚未登录，不能接受消息");
            }

        });
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx)
            throws Exception {
        LocalSession session = ctx.channel().attr(LocalSession.SESSION_KEY).get();

        if (null != session && session.isValid()) {
            session.close();
            sessionManager.removeLocalSession(session.getSessionId());
        }
    }
}
