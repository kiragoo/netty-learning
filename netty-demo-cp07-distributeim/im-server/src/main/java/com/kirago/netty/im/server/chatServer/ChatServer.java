package com.kirago.netty.im.server.chatServer;

import com.kirago.netty.im.common.codec.ProtoBufDecoder;
import com.kirago.netty.im.common.codec.ProtoBufEncoder;
import com.kirago.netty.im.common.concurrent.FutureTaskScheduler;
import com.kirago.netty.im.common.util.IOUtil;
import com.kirago.netty.im.server.distributed.ImWorker;
import com.kirago.netty.im.server.distributed.PeerManager;
import com.kirago.netty.im.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

@Service
@Data
@Slf4j
public class ChatServer {
    
    @Value("${server.port}")
    private int port;
    
    private NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    
    private NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    
    private ServerBootstrap b = new ServerBootstrap();
    
    @Autowired
    private LoginRequestHandler loginRequestHandler;
    
    @Autowired
    private ServerExceptionHandler serverExceptionHandler;
    
    @Autowired
    private RemoteNotificationHandler remoteNotificationHandler;
    
    @Autowired
    private ChatRedirectHandler chatRedirectHandler;

    public void run() {
        //1 设置reactor 线程
        b.group(bossGroup, workerGroup);
        //2 设置nio类型的channel
        b.channel(NioServerSocketChannel.class);
        //3 设置监听端口
        String ip = IOUtil.getHostAddress();
        b.localAddress(new InetSocketAddress(ip, port));
        //4 设置通道选项
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.ALLOCATOR,
                PooledByteBufAllocator.DEFAULT);

        //5 装配流水线
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            //有连接到达时会创建一个channel
            protected void initChannel(SocketChannel ch) throws Exception {
                // 管理pipeline中的Handler
                ch.pipeline().addLast("deCoder",new ProtoBufDecoder());
                ch.pipeline().addLast("enCoder",new ProtoBufEncoder());
                ch.pipeline().addLast("heartBeat",new HeartBeatServerHandler());
                // 在流水线中添加handler来处理登录,登录后删除
                ch.pipeline().addLast("loginRequest",loginRequestHandler);
                ch.pipeline().addLast("remoteNotificationHandler",remoteNotificationHandler);
                ch.pipeline().addLast("chatRedirect",chatRedirectHandler);
                ch.pipeline().addLast("serverException",serverExceptionHandler);
            }
        });
        // 6 开始绑定server
        // 通过调用sync同步方法阻塞直到绑定成功

        ChannelFuture channelFuture = null;
        boolean isStart = false;
        while (!isStart) {
            try {

                channelFuture = b.bind().sync();
                log.info("IM 启动, 端口为： " +
                        channelFuture.channel().localAddress());
                isStart = true;
            } catch (Exception e) {
                log.error("发生启动异常", e);
//                port++;
//                log.info("尝试一个新的端口：" + port);
//                b.localAddress(new InetSocketAddress(port));
            }
        }


        ImWorker.getInst().setLocalNode(ip, port);

        FutureTaskScheduler.add(() -> {
            /**
             * 启动节点
             */
            ImWorker.getInst().init();

            /**
             * 启动节点的管理
             */
            PeerManager.getInst().init();

        });
        try {
            // 7 监听通道关闭事件
            // 应用程序会一直等待，直到channel关闭
            ChannelFuture closeFuture =
                    channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (
                Exception e) {
            log.error("发生其他异常", e);
        } finally {
            // 8 优雅关闭EventLoopGroup，
            // 释放掉所有资源包括创建的线程
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }



}
