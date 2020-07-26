package cp02.echoserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MutilThreadEchoHandler implements Runnable{
    private final static Logger logger = Logger.getLogger(MutilThreadEchoHandler.class.getName());
    
    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    private static final int RECIEVING = 0, SENDING = 1;
    private int state = RECIEVING;
    // 引入线程池
    private static ExecutorService pool = Executors.newFixedThreadPool(4);
    
    public MutilThreadEchoHandler(Selector selector, SocketChannel socketChannel) throws IOException{
        this.socketChannel = socketChannel;
        socketChannel.configureBlocking(false);
        // 仅仅取的选择值，后设置感兴趣的IO事件
        selectionKey = socketChannel.register(selector, 0);
        // 将 handler 作为 selectorKey 的附件，方便 dispatch
        selectionKey.attach(this);
        // 向 selectorKey 注册Read事件
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }
    
    @Override
    public void run(){
        // 异步任务，在独立的线程池中执行
        pool.execute(new AsyncTask());
    }
    
    // 异步任务，不在Reactor线程中执行
    public synchronized void asyncRun(){
        try {
            if(state == SENDING){
                // 写入通道
                logger.info("【SENDING Selector】正在发送数据" + new String(byteBuffer.array()));
                socketChannel.write(byteBuffer);
                // 写完后，切换ByteBuffer模式切换成写模式
                byteBuffer.clear();
                // 写完后，注册read就绪事件
                selectionKey.interestOps(SelectionKey.OP_READ);
                // 写完后，切换 state状态
                state = RECIEVING;
            }else if( state == RECIEVING){
                int length = 0;
                while ((socketChannel.read(byteBuffer)) > 0){
                    logger.info(new String(byteBuffer.array(), 0, length)); 
                }
                byteBuffer.flip();
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                state = SENDING;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // 异步任务的内部类
    public class AsyncTask implements Runnable {
        
        @Override
        public void run(){
            MutilThreadEchoHandler.this.asyncRun();
        }
    }
    
}
