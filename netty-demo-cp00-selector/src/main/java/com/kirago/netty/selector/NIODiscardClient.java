package com.kirago.netty.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class NIODiscardClient {
    private static final Logger logger = Logger.getLogger(NIODiscardClient.class.getName());
    
    public static void main(String[] args) throws IOException {
        startClient();
    }
    
    private static void startClient() throws IOException {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 30000);
        SocketChannel socketChannel = SocketChannel.open(address);
        socketChannel.configureBlocking(false);
        
        while (!socketChannel.finishConnect()){
            // 不断自旋
        }
        logger.info("客户端连接成功");
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("hello word".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        socketChannel.shutdownOutput();
        socketChannel.close();
    }
}
