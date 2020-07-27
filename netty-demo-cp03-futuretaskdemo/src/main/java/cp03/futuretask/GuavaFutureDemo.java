package cp03.futuretask;

import com.google.common.util.concurrent.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class GuavaFutureDemo {
    
    private static final Logger logger = Logger.getLogger(GuavaFutureDemo.class.getName());
    public static final int SLEEP_GAP = 500;
    // 煮水
    private static class HotWaterJob implements Callable<Boolean>{

        @Override
        public Boolean call() throws Exception {
            try {
                logger.info("--》 开始煮水");
                Thread.sleep(SLEEP_GAP);
                logger.info("--》 煮水进行中");
            }catch (InterruptedException e){
                logger.info("发生异常中断");
                return false;
            }
            logger.info("--》 煮水结束");
            return true;
        }
    }
    // 清洗茶具
    private static class WashJob implements Callable<Boolean>{
        
        @Override
        public Boolean call() throws Exception{
            try {
                logger.info("--》 开始清洗");
                Thread.sleep(SLEEP_GAP);
                logger.info("清洗进行中");
            }catch (InterruptedException e){
                logger.info("发生异常");
                return false;
            }
            logger.info("--》 清洗结束");
            return true;
        }
    }
    
    // 泡茶线程
    private static class MainJob implements Runnable {
        
        boolean waterOk = false;
        boolean cupOk = false;
        
        int gap = SLEEP_GAP / 10;
        @Override
        public void run(){
            while (true){
                try {
                    Thread.sleep(gap);
                    logger.info(" --》 扯淡中");
                }catch (InterruptedException e){
                    logger.info(Thread.currentThread().getName() + "发生异常被中断");
                }
                if( waterOk && cupOk){
                    
                }
            }
        }
        
        private void drinkTea(Boolean wOk, Boolean hOk){
            if(wOk && hOk){
                logger.info("--》 泡茶喝，茶喝完");
                this.waterOk = false;
                this.gap = SLEEP_GAP * 100;
            }else if(!hOk){
                logger.info("--》 烧水失败");
            }else if(!wOk){
                logger.info("--》 清洗失败");
            }
        }
        
        
    }

    public static void main(String args[]) {

        //新起一个线程，作为泡茶主线程
        MainJob mainJob = new MainJob();
        Thread mainThread = new Thread(mainJob);
        mainThread.setName("主线程");
        mainThread.start();

        //烧水的业务逻辑
        Callable<Boolean> hotJob = new HotWaterJob();
        //清洗的业务逻辑
        Callable<Boolean> washJob = new WashJob();

        //创建java 线程池
        ExecutorService jPool =
                Executors.newFixedThreadPool(10);

        //包装java线程池，构造guava 线程池
        ListeningExecutorService gPool =
                MoreExecutors.listeningDecorator(jPool);

        //提交烧水的业务逻辑，取到异步任务
        ListenableFuture<Boolean> hotFuture = gPool.submit(hotJob);
        //绑定任务执行完成后的回调，到异步任务
        Futures.addCallback(hotFuture, new FutureCallback<Boolean>() {
            public void onSuccess(Boolean r) {
                if (r) {
                    mainJob.waterOk = true;
                }
            }

            public void onFailure(Throwable t) {
                logger.info("烧水失败，没有茶喝了");
            }
        },
          jPool  
        );
        //提交清洗的业务逻辑，取到异步任务

        ListenableFuture<Boolean> washFuture = gPool.submit(washJob);
        //绑定任务执行完成后的回调，到异步任务
        Futures.addCallback(washFuture, new FutureCallback<Boolean>() {
            public void onSuccess(Boolean r) {
                if (r) {
                    mainJob.cupOk = true;
                }
            }

            public void onFailure(Throwable t) {
                logger.info("杯子洗不了，没有茶喝了");
            }
        },
            jPool
        );
    }
}
