package com.kirago.zk.distributelock;

/**
* @Description:    java类作用描述
* @Author:         kirago
* @CreateDate:     2020/8/27 8:06 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public interface Lock {
    boolean lock();
    
    boolean unlock();
}
