package com.kirago.zk.publishsubscribe;

import com.kirago.zk.utills.ZKclient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.Test;

@Slf4j
@Data
public class ZKWatcherDemo {
    private String wokerPath = "/test/listener/remoteNode";
    private String subWorkPath = "test/listener/remoteNode/id-";
    
    @Test
    public void testWatcher(){
        CuratorFramework client = ZKclient.instance.getClient();
        boolean isExist = ZKclient.instance.isNodeExist(wokerPath);
        if(!isExist){
            ZKclient.instance.createNode(wokerPath, null);
        }
        try {
            Watcher w = new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    log.info("监听到的变化 WatchEvent: " + watchedEvent);
                }
            };
            byte[] content = client.getData()
                    .usingWatcher(w)
                    .forPath(wokerPath);
            log.info("监听节点内容: " + new String(content));

            client.setData().forPath(wokerPath,"第一次更改内容".getBytes());
            client.setData().forPath(wokerPath,"第二次更改内容".getBytes());

            Thread.sleep(Integer.MAX_VALUE);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            CloseableUtils.closeQuietly(client);
        }
    }
    
    @Test
    public void testWatchNode(){
        boolean isExist = ZKclient.instance.isNodeExist(wokerPath);
        if(isExist) ZKclient.instance.deleteNode(wokerPath);
        ZKclient.instance.createNode(wokerPath, null);
        CuratorFramework client = ZKclient.instance.getClient();
        try {
            NodeCache nodeCache = new NodeCache(client, wokerPath, false);
            NodeCacheListener nodeCacheListener = new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    ChildData childData = nodeCache.getCurrentData();
                    log.info("ZNode 节点状态改变, path:{}" ,childData.getPath());
                    log.info("ZNode 节点状态改变, data:{}", new String(childData.getData(),"UTF-8"));
                    log.info("ZNode 节点状态改变, state:{}", childData.getStat());
                }
            };
            nodeCache.getListenable().addListener(nodeCacheListener);
            nodeCache.start();

            client.setData().forPath(wokerPath,"第一次更改内容".getBytes());
            Thread.sleep(1000);

            client.setData().forPath(wokerPath,"第二次更改内容".getBytes());
            Thread.sleep(1000);


            client.setData().forPath(wokerPath,"第三次更改内容".getBytes());
            Thread.sleep(1000);

            Thread.sleep(Integer.MAX_VALUE);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            CloseableUtils.closeQuietly(client);
        }
    }
}
