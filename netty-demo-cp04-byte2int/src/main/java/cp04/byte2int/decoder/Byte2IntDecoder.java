package cp04.byte2int.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.logging.Logger;

/**
* @Description:    Netty decoder 示例
* @Author:         kirago
* @CreateDate:     2020/7/28 10:15 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class Byte2IntDecoder extends ByteToMessageDecoder {
    
    private static final Logger logger = Logger.getLogger(ByteToMessageDecoder.class.getName());
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while(in.readableBytes() >= 4){
            int i = in.readInt();
            logger.info("解码出一个整数: " + i);
            out.add(i);
        }
    }
}
