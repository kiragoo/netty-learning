package com.kirago.netty.im.server.handler;

import com.kirago.netty.im.common.protocol.Proto3Msg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
public class ImNodeHeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    //心跳的时间间隔，单位为s
    private static final int HEARTBEAT_INTERVAL = 100;

    //在Handler被加入到Pipeline时，开始发送心跳
    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
            throws Exception {

        Proto3Msg.ProtoMsg.Message.Builder mb =
                Proto3Msg.ProtoMsg.Message
                        .newBuilder()
                        .setType(Proto3Msg.ProtoMsg.HeadType.HEART_BEAT)
                        .setSessionId("unknown")
                        .setSequence(-1);
        Proto3Msg.ProtoMsg.Message message =   mb.buildPartial();
        Proto3Msg.ProtoMsg.MessageHeartBeat.Builder lb =
                Proto3Msg.ProtoMsg.MessageHeartBeat.newBuilder()
                        .setSeq(0)
                        .setJson("{\"from\":\"imNode\"}")
                        .setUid("-1");
        message.toBuilder().setHeartBeat(lb).build();

        //发送心跳
        heartBeat(ctx, message);
    }

    //使用定时器，发送心跳报文
    public void heartBeat(ChannelHandlerContext ctx,
                          Proto3Msg.ProtoMsg.Message heartbeatMsg) {
        ctx.executor().schedule(() -> {

            if (ctx.channel().isActive()) {
                log.info(" 发送 ImNode HEART_BEAT  消息 other");
                ctx.writeAndFlush(heartbeatMsg);

                //递归调用，发送下一次的心跳
                heartBeat(ctx, heartbeatMsg);
            }

        }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * 接受到服务器的心跳回写
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //判断消息实例
        if (null == msg || !(msg instanceof Proto3Msg.ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断类型
        Proto3Msg.ProtoMsg.Message pkg = (Proto3Msg.ProtoMsg.Message) msg;
        Proto3Msg.ProtoMsg.HeadType headType = pkg.getType();
        if (headType.equals(Proto3Msg.ProtoMsg.HeadType.HEART_BEAT)) {

            log.info(" imNode 收到回写的 HEART_BEAT  消息 from other node");

            return;
        } else {
            super.channelRead(ctx, msg);

        }

    }

}
