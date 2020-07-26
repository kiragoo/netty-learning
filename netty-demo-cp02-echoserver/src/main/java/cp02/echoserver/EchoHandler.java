package cp02.echoserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class EchoHandler implements Runnable{
    private final static Logger logger = Logger.getLogger(EchoHandler.class.getName());
    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private static final int RECIVING = 0, SENDING = 1;
    private int state = RECIVING;

    public EchoHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        this.selectionKey = socketChannel.register(selector, 0);
        // 将自己附带到 sk 中
        this.selectionKey.attach(this);
        // 注册Read就绪事件
        this.selectionKey.interestOps(SelectionKey.OP_READ);
        // 激活 selector 状态
        selector.wakeup();
    }

    public void run(){
        try {
            if(state == SENDING){
                // 写入到 channel
                socketChannel.write(byteBuffer);
                // 切换到可读模式
                byteBuffer.clear();
                
                selectionKey.interestOps(SelectionKey.OP_READ);
                state = RECIVING;
            }else if(state == RECIVING){
                int len = 0;
                logger.info(new String(byteBuffer.array(), 0, len));
                byteBuffer.flip();
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                state = SENDING;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
