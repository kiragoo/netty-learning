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
        this.selectionKey.attach(this);
        this.selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    public void run(){
        try {
            if(state == SENDING){
               socketChannel.write(byteBuffer);
               byteBuffer.clear();
               logger.info(" 【发送状态....】");
               selectionKey.interestOps(SelectionKey.OP_READ);
               state = RECIVING;
            }else if(state == RECIVING){
                int len = 0;
                logger.info(new String(byteBuffer.array(), 0, len));
                byteBuffer.flip();
                logger.info("【读取状态.....】");
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                state = SENDING;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
