package cp02.echoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class MutilThreadEchoServerReactor {
    
    private static final Logger logger = Logger.getLogger(MutilThreadEchoHandler.class.getName());

    private NioDemoConfig nioDemoConfig = new NioDemoConfig();

    // 初始化 ServerSocketChannel
    private ServerSocketChannel serverSocketChannel;

    // TODO
    private AtomicInteger next = new AtomicInteger(0);

    // selector集合，用于引入多个 selector
    Selector[] selectors = new Selector[2];

    // SubSelector 集合，引入多个 SubSelector
    SubReactor[] subReactors = null;

    /**
     *
     * @throws IOException
     */
    public MutilThreadEchoServerReactor() throws IOException {
        // 初始化多个 selector
        selectors[0] = Selector.open();
        selectors[1] = Selector.open();

        // 初始化  serverSocketChannel
        serverSocketChannel = ServerSocketChannel.open();

        // 初始化 address
        InetSocketAddress address = new InetSocketAddress(nioDemoConfig.getServerIp(), Integer.parseInt(nioDemoConfig.getServerPort()));

        // channel 绑定 address
        serverSocketChannel.socket().bind(address);

        // 配置为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 在 serverSocketChannel 中注册 sk，一个用于监控新连接事件
        SelectionKey sk = serverSocketChannel.register(selectors[0], SelectionKey.OP_ACCEPT);

        // 附件新连接 handler 到对应的 sk 中
        sk.attach(new AcceptorHandler());

        // 第一个子反应器
        SubReactor subReactor1 = new SubReactor(selectors[0]);
        // 第二个子反应器
        SubReactor subReactor2 = new SubReactor(selectors[1]);
        subReactors = new SubReactor[]{subReactor1, subReactor2};
    }

    private void startService(){
        new Thread(subReactors[0]).start();
        new Thread(subReactors[1]).start();
    }

    private class SubReactor implements Runnable{

        private final Selector selector;

        public SubReactor(Selector selector){
            this.selector = selector;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()){
                    selector.select();
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
                    while (selectionKeyIterator.hasNext()){
                        // Reactor 负责 dispatch 收到的事件
                        SelectionKey selectionKey = (SelectionKey) selectionKeyIterator.next();
                        dispatch(selectionKey);
                    }
                    selectionKeys.clear();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        private void dispatch(SelectionKey selectionKey){
            Runnable handler = (Runnable)selectionKey.attachment();
            // 调用之前 attach 绑定到选择器对应的handler 处理器对象
            if(handler != null){
                handler.run();
            }
        }
    }

    class AcceptorHandler implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if(socketChannel != null){
                    new MutilThreadEchoHandler(selectors[next.get()], socketChannel);
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
            if(next.incrementAndGet() == selectors.length){
                next.set(0);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        MutilThreadEchoServerReactor mutilThreadEchoServerReactor = new MutilThreadEchoServerReactor();
        mutilThreadEchoServerReactor.startService();
        logger.info("服务器已经启动！"); 
    }


}
