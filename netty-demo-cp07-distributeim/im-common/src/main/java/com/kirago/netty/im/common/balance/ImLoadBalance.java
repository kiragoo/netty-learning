package com.kirago.netty.im.common.balance;

import com.kirago.netty.im.common.constants.ServerConstants;
import com.kirago.netty.im.common.entity.PT.ImNode;
import com.kirago.netty.im.common.util.JsonUtil;
import com.kirago.netty.im.common.zk.ZKClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Slf4j
@Service
public class ImLoadBalance {

    //Zk客户端
    private CuratorFramework client = null;
    private String workParentPath;

    public ImLoadBalance() {
        this.client = ZKClient.instance.getClient();
        workParentPath= ServerConstants.WORKER_PARENT_PATH;
    }

    /**
     * 获取负载最小的IM节点
     *
     * @return
     */
    public ImNode getBestWorker() {
        List<ImNode> workers = getWorkers();

        log.info("全部节点如下：");
        workers.forEach(node -> {
            log.info("节点信息：{}", JsonUtil.object2JsonString(node));
        });
        return balance(workers);
    }

    /**
     * 按照负载排序
     *
     * @param items 所有的节点
     * @return 负载最小的IM节点
     */
    protected ImNode balance(List<ImNode> items) {
        if (items.size() > 0) {
            // 根据balance值由小到大排序
            Collections.sort(items);

            // 返回balance值最小的那个
            ImNode node = items.get(0);

            log.info("最佳的节点为：{}", JsonUtil.object2JsonString(node));
            return node;
        } else {
            return null;
        }
    }


    /**
     * 从zookeeper中拿到所有IM节点
     */
    protected List<ImNode> getWorkers() {

        List<ImNode> workers = new ArrayList<ImNode>();

        List<String> children = null;
        try {
            children = client.getChildren().forPath(workParentPath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        for (String child : children) {
            log.info("child: {}", child);
            byte[] payload = null;
            try {
                payload = client.getData().forPath(workParentPath+ "/" +child);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null == payload) {
                continue;
            }
            ImNode worker = JsonUtil.bytes2PoJo(payload, ImNode.class);
            workers.add(worker);
        }
        return workers;

    }
    /**
     * 从zookeeper中删除所有IM节点
     */
    public void removeWorkers() {


        try {
          client.delete().deletingChildrenIfNeeded().forPath(workParentPath);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}