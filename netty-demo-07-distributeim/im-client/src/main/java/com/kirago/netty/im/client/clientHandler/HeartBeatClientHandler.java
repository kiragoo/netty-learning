package com.kirago.netty.im.client.clientHandler;


import com.kirago.netty.im.client.client.ClientSession;
import com.kirago.netty.im.client.protoBuilder.HeartBeatMsgBuilder;
import com.kirago.netty.im.common.entity.PT.UserPT;
import com.kirago.netty.im.common.protocol.Proto3Msg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
@Service("HeartBeatClientHandler")
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {
    //心跳的时间间隔，单位为s
    private static final int HEARTBEAT_INTERVAL = 100;

    //在Handler被加入到Pipeline时，开始发送心跳
    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
            throws Exception {
        ClientSession session = ClientSession.getSession(ctx);
        UserPT user = session.getUser();
        HeartBeatMsgBuilder builder =
                new HeartBeatMsgBuilder(user, session);

        Proto3Msg.ProtoMsg.Message message = builder.buildMsg();
        //发送心跳
        heartBeat(ctx, message);
    }

    //使用定时器，发送心跳报文
    public void heartBeat(ChannelHandlerContext ctx,
                          Proto3Msg.ProtoMsg.Message heartbeatMsg) {
        ctx.executor().schedule(() -> {

            if (ctx.channel().isActive()) {
                log.info(" 发送 HEART_BEAT  消息 to server");
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
        if (!(msg instanceof Proto3Msg.ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断类型
        Proto3Msg.ProtoMsg.Message pkg = (Proto3Msg.ProtoMsg.Message) msg;
        Proto3Msg.ProtoMsg.HeadType headType = pkg.getType();
        if (headType.equals(Proto3Msg.ProtoMsg.HeadType.HEART_BEAT)) {

            log.info(" 收到回写的 HEART_BEAT  消息 from server");

            return;
        } else {
            super.channelRead(ctx, msg);

        }

    }

}
