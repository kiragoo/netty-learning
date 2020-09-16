package com.kirago.netty.im.client.clientHandler;

import com.kirago.netty.im.client.client.ClientSession;
import com.kirago.netty.im.client.client.CommandController;
import com.kirago.netty.im.common.constants.ProtoInstant;
import com.kirago.netty.im.common.protocol.Proto3Msg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@ChannelHandler.Sharable
@Service
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    CommandController commandController;
    @Autowired
    HeartBeatClientHandler heartBeatClientHandler;
    /**
     * 业务逻辑处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        //判断消息实例
        if (!(msg instanceof Proto3Msg.ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断类型
        Proto3Msg.ProtoMsg.Message pkg = (Proto3Msg.ProtoMsg.Message) msg;
        Proto3Msg.ProtoMsg.HeadType headType = ((Proto3Msg.ProtoMsg.Message) msg).getType();
        if (!headType.equals(Proto3Msg.ProtoMsg.HeadType.LOGIN_RESPONSE)) {
            super.channelRead(ctx, msg);
            return;
        }


        //判断返回是否成功
        Proto3Msg.ProtoMsg.LoginResponse info = pkg.getLoginResponse();

        ProtoInstant.ResultCodeEnum result =
                ProtoInstant.ResultCodeEnum.values()[info.getCode()];

        if (!result.equals(ProtoInstant.ResultCodeEnum.SUCCESS)) {
            log.info(result.getDesc());
            log.info("step3：登录Netty 服务节点失败");
        } else {

            ClientSession session =
                    ctx.channel().attr(ClientSession.SESSION_KEY).get();
            session.setSessionId(pkg.getSessionId());
            session.setLogin(true);

            log.info("step3：登录Netty 服务节点成功");
            commandController.notifyCommandThread();

            ctx.channel().pipeline().addAfter("loginResponseHandler","heartBeatClientHandler",heartBeatClientHandler);
            ctx.channel().pipeline().remove("loginResponseHandler");

        }


    }


}
