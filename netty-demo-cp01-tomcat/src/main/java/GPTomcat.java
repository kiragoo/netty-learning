import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import netty.demo.cp01.tomcat.common.GPRequest;
import netty.demo.cp01.tomcat.common.GPResponse;
import netty.demo.cp01.tomcat.common.GPServlet;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GPTomcat {

    private int port = 8000;

    private Map<String, GPServlet> servletMapping = new HashMap<String, GPServlet>();

    private Properties webxml = new Properties();

    private void init(){
        try {
            String WEB_INF = this.getClass().getResource("/").getPath();

            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");

            webxml.load(fis);
            for(Object k: webxml.keySet()){
                String key = k.toString();
                if(key.endsWith(".url")){
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");

                    GPServlet obj = (GPServlet) Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(){
        init();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();

            server.group(bossGroup, workerGroup)
                    .channel(NioSctpServerChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel client) throws Exception{
                            client.pipeline().addLast(new HttpResponseEncoder());
                            client.pipeline().addLast(new HttpRequestDecoder());
                            client.pipeline().addLast(new GPTomcatHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = server.bind(port).sync();
            System.out.println("GPTomcat 已经启动, 启动端口: " + port);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public class GPTomcatHandler  extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
            if( msg instanceof HttpRequest) {
                System.out.println("hello");
                HttpRequest req = (HttpRequest) msg;

                GPRequest request = new GPRequest(ctx, req);

                GPResponse response = new GPResponse(ctx, req);

                String url = request.getUrl();

                if(servletMapping.containsKey(url)){
                    servletMapping.get(url).service(request, response);
                }else {
                    response.write("404 - Not Fount");
                }

            }
        }
    }

    public static void main(String[] args){
        new GPTomcat().start();
    }
}
