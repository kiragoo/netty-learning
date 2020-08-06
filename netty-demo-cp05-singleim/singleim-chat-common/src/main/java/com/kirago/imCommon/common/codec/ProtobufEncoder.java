package com.kirago.imCommon.common.codec;

import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtobufEncoder extends MessageToByteEncoder<ProtoMsg3.Message> {
    
    @Override
    protected void encode(ChannelHandlerContext ctx, ProtoMsg3.Message msg, ByteBuf out) throws Exception{
        
        byte[] bytes = msg.toByteArray();
        /*
        加密消息体
        ThreeDES des = channel.channel().attr(AppAttrKeys.ENCRYPT).get();
        byte[] encryptByte = des.encrypt(bytes);
        */
        int length = bytes.length;
        
        ByteBuf byteBuf = ctx.alloc().buffer(2+ length);
        
        // 现将消息头写入
        byteBuf.writeShort(length);

        // 消息体中包含我们要发送的数据
        byteBuf.writeBytes(bytes);
        out.writeBytes(byteBuf);

        log.debug("send "
                + "[remote ip:" + ctx.channel().remoteAddress()
                + "][total length:" + length
                + "][bare length:" + msg.getSerializedSize() + "]");

        if(byteBuf.refCnt()>0)
        {
            log.debug("释放临时缓冲");
            byteBuf.release();
        }
    }
}
