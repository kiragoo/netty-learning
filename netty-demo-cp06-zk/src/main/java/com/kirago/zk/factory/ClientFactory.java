package com.kirago.zk.factory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
* @description:    java类作用描述
* @author:         kirago
* @date:     2020/8/27 3:18 下午
* @updateRemark:   修改内容
* @version:        1.0
*/
public class ClientFactory {
    
    public static CuratorFramework createSimple(String connection){
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        return CuratorFrameworkFactory.newClient(connection,retryPolicy);
    }
    
    public static CuratorFramework createWithOptions(String connection, RetryPolicy retryPolicy, int connectionTimeoutMs, int sessionTimeoutMs ){
        return CuratorFrameworkFactory.builder()
                .connectString(connection)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(connectionTimeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .build();
    }
}
