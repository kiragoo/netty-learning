package com.kirago.netty.oio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Handler;

public class ConnectionPerThread implements Runnable {
    
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(3000);
            while (!Thread.interrupted()){
                Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static class Handler implements Runnable{
        private final Socket socket ;
        
        public Handler(Socket socket){
            this.socket = socket;
        }
        
        public void run(){
            while (true){
                try {
                    byte[] in = new byte[1024];
                    socket.getInputStream().read(in);
                    byte[] out = null;
                    socket.getOutputStream().write(out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
