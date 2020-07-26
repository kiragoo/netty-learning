package cp03.joindemo;

import java.util.logging.Logger;

/**
* @Description:    Thread join demo
* @Author:         kirago
* @CreateDate:     2020/7/26 10:39 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class JoinDemo {
    
    private static final int SLEEP_GAP = 500;
    private static final Logger logger = Logger.getLogger(JoinDemo.class.getName());
    
//    public static String getCurrentThreadName(){
//        return Thread.currentThread().getName();
//    }
    
    // 烧水线程
    private static class HotWaterThread extends Thread{
        
        public HotWaterThread(){
            super("Thread Name --> HotWaterThread");
        }

        @Override
        public void run() {
            try {
                logger.info("--> 开始烧水线程");
                Thread.sleep(SLEEP_GAP * 2);
                logger.info("--> 水开了");
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
            logger.info("--> 烧水结束");
            
        }
    }
    
    // 清洗茶具线程
    private static class WashThread extends Thread{
        
        public WashThread(){
            super("Thread Name --> WashTread");
        }

        @Override
        public void run() {
            try {
                logger.info("--> 开始清洗");
                Thread.sleep(SLEEP_GAP);
                logger.info("--> 清洗进行中");
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }
            logger.info("--> 清洗结束");
        }
    }
    
    public static void main(String[] args){
        Thread h = new HotWaterThread();
        Thread w = new WashThread();
        
        logger.info("--> 流程开始");
        
        h.start();
        w.start();
        
        try {
            h.join();
            w.join();
            Thread.currentThread().setName("主线程");
            logger.info("--> 开始泡茶流程");
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
        logger.info("--> 流程结束");
    }
}
