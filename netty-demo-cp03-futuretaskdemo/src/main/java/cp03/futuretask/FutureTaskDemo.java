package cp03.futuretask;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

/**
* @Description:    java类作用描述
* @Author:         kirago
* @CreateDate:     2020/7/26 11:12 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class FutureTaskDemo {
    
    private static final Logger logger = Logger.getLogger(FutureTaskDemo.class.getName());
    
    private static final int SLEEEP_GAP = 500;
    
    // 煮水
    private static class HotWaterJob implements Callable<Boolean>{

        @Override
        public Boolean call() throws Exception {
            try {
                logger.info("--> 开始煮水线程 ");
                Thread.sleep(SLEEEP_GAP);
                logger.info("--> 水开了 ");
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            logger.info("--> 煮水结束");
            return true;
        }
    }
    
    // 清洗茶具
    private static class WashJob implements Callable<Boolean>{

        @Override
        public Boolean call() throws Exception {
            try {
                logger.info("--> 开始清洗茶具线程");
                Thread.sleep(SLEEEP_GAP);
                logger.info("--> 清洗进行中");
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            logger.info("--> 清洗结束");
            return true;
        }
    }

    /**
     * 
     * @param hwJob
     * @param wJob
     */
    private static void drinkTea(boolean hwJob, boolean wJob){
        if(hwJob&&wJob){
            logger.info("--> 泡茶喝");
        }else if(!hwJob){
            logger.info("--> 水没烧好");
        }else if(!wJob){
            logger.info("--> 茶具没洗");
        }
    }
    
    public static void main(String[] args){
        Callable<Boolean> h = new HotWaterJob();
        FutureTask<Boolean> hf = new FutureTask<>(h);
        Thread ht = new Thread(hf);
        
        Callable<Boolean> w = new WashJob();
        FutureTask<Boolean> wf = new FutureTask<>(w);
        Thread wt = new Thread(wf);

        logger.info("--> 流程开始");
        ht.start();
        wt.start();
        
        try {
            boolean hwOk = hf.get();
            boolean wOk = wf.get();
            drinkTea(hwOk, wOk);
        }catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
        logger.info("--> 流程结束");
        
    }
    
}
