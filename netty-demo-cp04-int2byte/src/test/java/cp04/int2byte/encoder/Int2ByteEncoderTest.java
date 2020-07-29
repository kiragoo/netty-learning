package cp04.int2byte.encoder;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

public class Int2ByteEncoderTest {
    
    @Test
    public void testInt2ByteEncoder(){
        ChannelInitializer channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel channel) throws Exception {
                channel.pipeline().addLast(new Int2ByteEncoder());
            }
        };
        EmbeddedChannel ch = new EmbeddedChannel(channelInitializer);
        for(int i=0;i<100;i++){
            ch.write(i);
        }
        ch.flush();
        ByteBuf byteBuf = (ByteBuf) ch.readOutbound();
        try {
            Thread.sleep(Integer.MAX_VALUE);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    

}