package cp02.echoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>
 * 回显服务端设计
 * EchoSerReactor implements {@link Runnable} {
 * 
 *     构造函数{
 *          selector {@link Selector}
 *          serverSocketChannel {@link ServerSocketChannel}
 *          serverSocketChannel 绑定 ip + port
 *          serverSocketChannel 配置为非阻塞
 *          serverSocketChannel 将对应的事件注册到 selector 中
 *          sk {@link SelectionKey} 附属相关 Handler【AcceptorHandler】
 *     }
 *     
 * }
 * </p>
 */
class EchoServerReactor implements Runnable{

    private final static Logger logger = Logger.getLogger(EchoServerReactor.class.getName());

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private NioDemoConfig nioDemoConfig = new NioDemoConfig();

    public EchoServerReactor() throws IOException {

        // 初始化 selector
        selector = selector.open();
        // 初始化 serverSocketChannel
        serverSocketChannel = ServerSocketChannel.open();
        // 初始化 address
        InetSocketAddress address = new InetSocketAddress(nioDemoConfig.getServerIp(), Integer.parseInt(nioDemoConfig.getServerPort()));
        // 绑定 address
        serverSocketChannel.socket().bind(address);
        // 配置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 注册 serverSocketChannel 的 accept 事件
        SelectionKey sk = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 将新的acceptHandler 绑定到 sk 中
        sk.attach(new AcceptorHandler());

    }

    public void run() {
        try {
            while (!Thread.interrupted()){
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey sk = iterator.next();
                    // sk 对应事件的分发
                    dispatch(sk);
                }
                selectionKeys.clear();
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    void dispatch(SelectionKey sk){
        // 取出对应 sk 绑定的 handler
        Runnable handler = (Runnable) sk.attachment();
        if(handler != null){
            // 另起 Handler 线程进行对应 Handler 逻辑处理
            handler.run();
        }
    }

    class AcceptorHandler implements Runnable {
        public void run(){
            try {
                SocketChannel channel = serverSocketChannel.accept();
                if(channel!= null){
                    // 需要将对应 selector 及 channel 中传递到 Handler 中
                    new EchoHandler(selector, channel);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException{
        new Thread(new EchoServerReactor()).start();
        logger.info("echoserver start");
    }
}