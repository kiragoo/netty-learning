package com.kirago.zk.basicoperator;

import com.kirago.zk.factory.ClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class CRUD {
    
    private static final String ZK_ADDRESS = "127.0.0.1:2181";
    private static final String NODE_PATH = "/test/CRUD/rmNode-1";
    
    @Test
    public void checkNode(){
        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
        
        try {
            client.start();
            
            Stat stat = client.checkExists().forPath(NODE_PATH);
            if(stat == null){
                log.info("节点不存在");
            }else {
                log.info("节点存在:" + stat.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            CloseableUtils.closeQuietly(client);
        }
    }
    
    @Test
    public void createNode(){
        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
        try {
            client.start();
            
            String data = "hello";
            byte[] payload = data.getBytes("utf-8");
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(NODE_PATH, payload);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            CloseableUtils.closeQuietly(client);
        }
    }
    
    @Test
    public void readNode(){
        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
        try {
            client.start();
            
            Stat stat = client.checkExists().forPath(NODE_PATH);
            if(stat != null){
                byte[] playload = client.getData().forPath(NODE_PATH);
                String data = new String(playload,"utf-8");
                log.info("data: " + data);
                String parentPath = "/test";
                List<String> children = client.getChildren().forPath(parentPath);
                for(String child:children){
                    log.info("child: " + child);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            CloseableUtils.closeQuietly(client);
        }
    }
    
    @Test
    public void updateNode(){
        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
        try {
            client.start();
            
            String data = "hello word";
            byte[] playload = data.getBytes("UTF-8");
            client.setData().forPath(NODE_PATH, playload);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            CloseableUtils.closeQuietly(client);
        }
    }
    
    @Test
    public void asyncNode(){
        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
        try {
            AsyncCallback.StringCallback callback = new AsyncCallback.StringCallback() {
                @Override
                public void processResult(int i, String s, Object o, String s1) {
                    System.out.println(
                            "i= " + i + " | "
                            + "s= " +s+ " | "
                            + "o= " +o+ " | "
                            + "s1= " +s1
                    );
                }
            };
            client.start();
            
            String data = "hello, everybody";
            byte[] playload = data.getBytes("UTF-8");
            client.setData()
                    .inBackground(callback)
                    .forPath(NODE_PATH, playload);
            Thread.sleep(10000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            CloseableUtils.closeQuietly(client);
        }
    }
    
    @Test
    public void deleteNode(){
        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
        try {
            client.start();
            
            client.delete().forPath(NODE_PATH);
            String parentPath = "/test";
            List<String> children = client.getChildren().forPath(parentPath);
            for(String child:children){
                log.info("child: " + child);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            CloseableUtils.closeQuietly(client);
        }
    }
    
}
