package cp04.int2byte.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.logging.Logger;

/**
* @Description:    Integer To Message Encoder
* @Author:         kirago
* @CreateDate:     2020/7/29 9:43 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class Int2ByteEncoder extends MessageToByteEncoder<Integer> {
    
    private static final Logger logger = Logger.getLogger(Int2ByteEncoder.class.getName());
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Integer in, ByteBuf out) throws Exception {
        out.writeInt(in);
        logger.info("编码信息为: " + in);
        
    }
}
