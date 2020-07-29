package cp04.byte2string.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

public class StringProcess extends ChannelInboundHandlerAdapter {
    
    private static final Logger logger = Logger.getLogger(StringProcess.class.getName());
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        
        String s = (String) msg;
        logger.info("解码内容为: " + s);
    }
}
