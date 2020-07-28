package cp04.byte2int;

import cp04.byte2int.decoder.Byte2IntDecoder;
import cp04.byte2int.handler.IntegerProcessHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

/**
* @Description:    整数解码器使用实例
* @Author:         kirago
* @CreateDate:     2020/7/28 10:22 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class Byte2IntegerDecoderTest {
    
    @Test
    public void testByte2IntDecoder(){
        ChannelInitializer ci = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ec) throws Exception {
                ec.pipeline().addLast(new Byte2IntDecoder());
                ec.pipeline().addLast(new IntegerProcessHandler());
            }
        };
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(ci);
        for (int i=0;i<100;i++){
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeInt(i);
            embeddedChannel.writeInbound(byteBuf);
        }
        
        try {
            Thread.sleep(Integer.MAX_VALUE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
