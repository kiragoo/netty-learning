package com.kirago.netty.im.client.clientHandler;


import com.kirago.netty.im.client.ClientSender.LoginSender;
import com.kirago.netty.im.common.protocol.Proto3Msg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@ChannelHandler.Sharable
@Service
public class ChatMsgHandler extends ChannelInboundHandlerAdapter {

    private LoginSender sender;

    public ChatMsgHandler(LoginSender sender) {
        this.sender = sender;
    }


    /**
     * 业务逻辑处理
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
        if (!headType.equals(Proto3Msg.ProtoMsg.HeadType.MESSAGE_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }

        Proto3Msg.ProtoMsg.MessageRequest req= pkg.getMessageRequest();
        String content=req.getContent();
        String userId=req.getFrom();

        log.info(" 收到消息 from uid:{} -> {}",userId , content);
  }


}
