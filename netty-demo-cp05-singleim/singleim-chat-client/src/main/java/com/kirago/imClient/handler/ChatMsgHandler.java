package com.kirago.imClient.handler;


import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Service;

/**
* @Description:    接受消息处理器
* @Author:         kirago
* @CreateDate:     2020/8/8 9:49 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
@ChannelHandler.Sharable
@Service("ChatMsgHandler")
public class ChatMsgHandler extends ChannelInboundHandlerAdapter {

    public ChatMsgHandler() {

    }


    /**
     * 业务逻辑处理
     */
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
        if (!headType.equals(ProtoMsg3.HeadType.MESSAGE_REQUEST)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg3.MessageRequest req = pkg.getMessageRequest();
        String content = req.getContent();
        String uid = req.getFrom();

        System.out.println(" 收到消息 from uid:" + uid + " -> " + content);
    }


}


