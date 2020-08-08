package com.kirago.imClient.handler;

import com.kirago.imClient.client.ClientSession;
import com.kirago.imClient.protoBuilder.HeartBeatMsgBuilder;
import com.kirago.imCommon.common.bean.User;
import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
* @Description:    心跳保持处理器
* @Author:         kirago
* @CreateDate:     2020/8/8 10:00 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
@Slf4j
@ChannelHandler.Sharable
@Service("HeartBeatClientHandler")
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {

    //心跳的时间间隔，单位为s
    private static final int HEARTBEAT_INTERVAL = 100;

    /**
    * @Description: 将心跳 handler 加入到 Pipline 中时开始发送心跳
    * @Param: ChannelHandlerContext
    * @return: 
    **/
    @Override
    public void handlerAdded(ChannelHandlerContext ctx)
            throws Exception {
        ClientSession session = ClientSession.getSession(ctx);
        User user = session.getUser();
        HeartBeatMsgBuilder builder =
                new HeartBeatMsgBuilder(user, session);

        ProtoMsg3.Message message = builder.buildMsg();
        //发送心跳
        heartBeat(ctx, message);
    }

    /**
    * @Description: 使用定时器发送心跳报文
    * @Param: ChannelHandlerContext, ProtoMsg3.Message
    * @return: 
    **/
    private void heartBeat(ChannelHandlerContext ctx,
                          ProtoMsg3.Message heartbeatMsg) {
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
    * @Description: 接受到服务器的心跳回写
    * @Param: 
    * @return: 
    **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //判断消息实例
        if (null == msg || !(msg instanceof ProtoMsg3.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断类型
        ProtoMsg3.Message pkg = (ProtoMsg3.Message) msg;
        ProtoMsg3.HeadType headType = pkg.getType();
        if (headType.equals(ProtoMsg3.HeadType.HEART_BEAT)) {

            log.info(" 收到回写的 HEART_BEAT  消息 from server");

            return;
        } else {
            super.channelRead(ctx, msg);

        }

    }
}
