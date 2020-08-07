package com.kirago.imServer.handler;

import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import com.kirago.imCommon.concurrent.FutureTaskScheduler;
import com.kirago.imServer.server.ServerSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class HeartBeatServerHandler  extends IdleStateHandler {

    private static final int READ_IDLE_GAP = 150;

    public HeartBeatServerHandler() {
        super(READ_IDLE_GAP, 0, 0, TimeUnit.SECONDS);

    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        //判断消息实例
        if (null == msg || !(msg instanceof ProtoMsg3.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg3.Message pkg = (ProtoMsg3.Message) msg;
        //判断消息类型
        ProtoMsg3.HeadType headType = pkg.getType();
        if (headType.equals(ProtoMsg3.HeadType.HEART_BEAT)) {
            //异步处理,将心跳包，直接回复给客户端
            FutureTaskScheduler.add(() -> {
                if (ctx.channel().isActive()) {
                    ctx.writeAndFlush(msg);
                }
            });

        }
        super.channelRead(ctx, msg);

    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        System.out.println(READ_IDLE_GAP + "秒内未读到数据，关闭连接");
        ServerSession.closeSession(ctx);
    }
}
