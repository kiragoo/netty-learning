package netty.demo.cp01.tomcat.common;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

public class GPResponse {

    private ChannelHandlerContext ctx;

    private HttpRequest req;

    public GPResponse(ChannelHandlerContext ctx, HttpRequest req){
        this.ctx = ctx;
        this.req = req;
    }

    public void write(String out) throws Exception{
        try {
            if (out == null || out.length() == 0){
                return;
            }
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(out.getBytes(StandardCharsets.UTF_8)));
            response.headers().set("Content-type", "text/html");
            ctx.write(response);
        }finally {
            ctx.flush();
            ctx.close();
        }
    }


}
