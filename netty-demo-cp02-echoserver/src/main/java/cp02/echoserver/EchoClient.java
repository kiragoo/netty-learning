package cp02.echoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

public class EchoClient {

    private final static Logger logger = Logger.getLogger(EchoClient.class.getName());

    private final static SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    private static NioDemoConfig nioDemoConfig;

    static {
        try {
            nioDemoConfig = new NioDemoConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public EchoClient() throws IOException {
    }

    public void start() throws IOException{
        InetSocketAddress inetSocketAddress = new InetSocketAddress(nioDemoConfig.getServerIp(), Integer.parseInt(nioDemoConfig.getServerPort()));

        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.configureBlocking(false);

        while (!socketChannel.finishConnect()){
            logger.info("。。。。。");
        }

        logger.info("客户端成功启动");

        Processer processer = new Processer(socketChannel);
        new Thread(processer).start();
    }

    private static class Processer implements Runnable{
        private final Selector selector;
        private final SocketChannel socketChannel;

        public Processer(SocketChannel socketChannel) throws IOException {
            this.selector = Selector.open();
            this.socketChannel = socketChannel;
            socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }

        public void run(){
            try {
                while (!Thread.interrupted()) {
                    selector.select();
                    Set<SelectionKey> selected = selector.selectedKeys();
                    Iterator<SelectionKey> it = selected.iterator();
                    while (it.hasNext()) {
                        SelectionKey sk = it.next();
                        if (sk.isWritable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(Integer.parseInt(nioDemoConfig.getSendBufferSize()));

                            Scanner scanner = new Scanner(System.in);
                            logger.info("请输入发送内容:");
                            if (scanner.hasNext()) {
                                SocketChannel socketChannel = (SocketChannel) sk.channel();
                                String next = scanner.next();
                                buffer.put((formatter.format(new Date(System.currentTimeMillis())) + " >>" + next).getBytes());
                                buffer.flip();
                                // 操作三：通过DatagramChannel数据报通道发送数据
                                socketChannel.write(buffer);
                                buffer.clear();
                            }

                        }
                        if (sk.isReadable()) {
                            // 若选择键的IO事件是“可读”事件,读取数据
                            SocketChannel socketChannel = (SocketChannel) sk.channel();

                            //读取数据
                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                            int length = 0;
                            while ((length = socketChannel.read(byteBuffer)) > 0) {
                                byteBuffer.flip();
                                logger.info("server echo:" + new String(byteBuffer.array(), 0, length));
                                byteBuffer.clear();
                            }

                        }
                        //处理结束了, 这里不能关闭select key，需要重复使用
                        //selectionKey.cancel();
                    }
                    selected.clear();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new EchoClient().start();
    }
}
