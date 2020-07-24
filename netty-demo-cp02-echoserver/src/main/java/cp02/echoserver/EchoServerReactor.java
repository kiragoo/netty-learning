package cp02.echoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

class EchoServerReactor implements Runnable{

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private NioDemoConfig nioDemoConfig = new NioDemoConfig();

    public EchoServerReactor() throws IOException {
        selector = selector.open();
        serverSocketChannel = ServerSocketChannel.open();

        InetSocketAddress address = new InetSocketAddress(nioDemoConfig.getServerIp(), Integer.parseInt(nioDemoConfig.getServerIp()));
        serverSocketChannel.socket().bind(address);

        serverSocketChannel.configureBlocking(false);

        SelectionKey sk = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

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
                    dispatch(sk);
                }
                selectionKeys.clear();
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    void dispatch(SelectionKey sk){
        Runnable handler = (Runnable) sk.attachment();
        if(handler != null){
            handler.run();
        }
    }

    class AcceptorHandler implements Runnable {
        public void run(){
            try {
                SocketChannel channel = serverSocketChannel.accept();
                if(channel!= null){
                    new EchoHandler(selector, channel);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException{
        new Thread(new EchoServerReactor()).start();
    }
}