package com.kirago.imCommon.common.codec;

import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ProtobufDecoder extends ByteToMessageDecoder {
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception{
        
        //标记下当前指针的位置
        in.markReaderIndex();
        
        //判断包头的长度
        if (in.readableBytes() < 2){
            // 不够包头
            return;
        }
        
        //读取传过来的消息的长度
        int length = in.readShort();
        
        if (length<2){
            // 长度小于2，非法数据
            ctx.close();
        }
        
        if (length<in.readableBytes()){
            // 读取的消息体的长度小于传过来的消息长度，重新读取位置
            in.resetReaderIndex();
            return;
        }

        byte[] array ;
        int offset=0;
        if (in.hasArray())
        {
            //堆缓冲
//            offset = in.arrayOffset() + in.readerIndex();
            ByteBuf slice=in.slice();
            array=slice.array();
        }
        else
        {
            //直接缓冲
            array = new byte[length];
            in.readBytes( array, 0, length);
        }

        // 字节转成对象
        ProtoMsg3.Message outmsg =
                ProtoMsg3.Message.parseFrom(array);


        if (outmsg != null)
        {
            // 获取业务消息头
            out.add(outmsg);
        }

    }
}
