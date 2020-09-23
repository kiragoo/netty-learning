package com.kirago.netty.filechannel;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileNIOCopyDemo {
    
    private static String SRC_PATH = "com/kirago/netty/filechannel/a.txt";
    
    private static String DEST_PATH = "b.txt";
    
    public static void main(String[] args){
        nioCopyResourceFiles();
    }
    
    public static void nioCopyResourceFiles(){
        // 获取资源路径下的文件路径
        String srcPath = IOUtil.getResourcePath(SRC_PATH);
        System.out.println("源文件路径: " + srcPath);
        
        String destPath = IOUtil.builderResourcePath(DEST_PATH);
        System.out.println("目的文件路径: " + destPath);
        
        nioCopyFile(srcPath, destPath);
    }
    
    public static void nioCopyFile(String src, String dest){
        File srcFile = new File(src);
        File destFile = new File(dest);
        try {
            if(!destFile.exists()){
                destFile.createNewFile();
            }
            long startTime = System.currentTimeMillis();
            FileInputStream fis = null;
            FileOutputStream fos = null;
            FileChannel inChannel = null;
            FileChannel outChannel = null;
            try {
                fis = new FileInputStream(srcFile);
                fos = new FileOutputStream(destFile);
                inChannel = fis.getChannel();
                outChannel = fos.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                while (inChannel.read(byteBuffer) != -1){
                    byteBuffer.flip();
                    int outLength = -1;
                    while ((outLength = outChannel.write(byteBuffer)) != 0){
                        System.out.println("写入的字节数: " + outLength);
                    }
                    byteBuffer.clear();
                }
                outChannel.force(true);
            } finally {
                IOUtil.closeQuietly(outChannel);
                IOUtil.closeQuietly(fos);
                IOUtil.closeQuietly(inChannel);
                IOUtil.closeQuietly(fis);
            }
            long endTime = System.currentTimeMillis();
            System.out.println(" 复制经历的毫秒数为: " + (endTime-startTime) );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
