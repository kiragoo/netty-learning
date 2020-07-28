package cp04.byte2int.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

/**
* @Description:    对应 byte2intdecoder 的业务逻辑处理器
* @Author:         kirago
* @CreateDate:     2020/7/28 10:18 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class IntegerProcessHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(IntegerProcessHandler.class.getName());
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Integer integer = (Integer) msg;
        logger.info("打印出一个整数: " + integer);
    }
}
