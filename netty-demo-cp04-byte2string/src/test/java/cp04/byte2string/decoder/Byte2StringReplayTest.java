package cp04.byte2string.decoder;

import cp04.byte2string.handler.StringProcess;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Random;

/**
* @Description:    java类作用描述
* @Author:         kirago
* @CreateDate:     2020/7/29 9:01 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class Byte2StringReplayTest {
    private String msg = "你好我是你大爷哦！";
    
    @Test
    public void testByte2StringReplay() {
        ChannelInitializer ch = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel embeddedChannel) throws Exception {
                embeddedChannel.pipeline().addLast(new Byte2StringReplay());
                embeddedChannel.pipeline().addLast(new StringProcess());
            }
        };
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(ch);
        byte[] bytes = msg.getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < 10; i++) {
            int ranInt = new Random().nextInt(5);
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeInt(bytes.length * ranInt);
            for (int j = 0; j < ranInt; j++) {
                byteBuf.writeBytes(bytes);
            }
            embeddedChannel.writeInbound(byteBuf);
        }
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
