package cp04.byte2string.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class Byte2StringReplay extends ReplayingDecoder<Byte2StringReplay.Status> {
    
    public enum Status {
        STATUS_1,
        STATUS_2
    }
    
    private int length;
    private byte[] inBytes;
    
    public Byte2StringReplay(){
        super(Status.STATUS_1);
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        
        switch (state()){
            case STATUS_1:
                length = in.readInt();
                inBytes = new byte[length];
                checkpoint(Status.STATUS_2);
                break;
            case STATUS_2:
                in.readBytes(inBytes, 0 , length);
                out.add(new String(inBytes, "UTF-8"));
                checkpoint(Status.STATUS_1);
                break;
            default:
                break;
        }
        
    }
}
