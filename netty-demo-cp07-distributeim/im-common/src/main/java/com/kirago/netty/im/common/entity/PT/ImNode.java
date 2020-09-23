package com.kirago.netty.im.common.entity.PT;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
* @description:    IMNode 节点类
* @author:         kirago
* @date:     2020/9/14 3:58 下午
* @updateRemark:   修改内容
* @version:        1.0
*/

@Data
@NoArgsConstructor
public class ImNode implements Comparable<ImNode>, Serializable {

    private static final long serialVersionUID = -6657292903641494978L;

    /**
     * woker im node id,created by zk
     */
    private long id;

    /**
     * the numbers of netty service
     */
    private AtomicInteger balance ;

    /**
     * the ip of netty server
     */
    private String host;

    /**
     * the port of netty server
     */
    private Integer port;
    
    public ImNode(String host,Integer port) {
        this.host = host;
        this.port = port;
    }
    
    @Override
    public String toString(){
        return "ImNode{" +
                "id='" + id + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ",balance='" + balance + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImNode node = (ImNode) o;
        return Objects.equals(host, node.host) &&
                Objects.equals(port, node.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, host, port);
    }

    /**
     * asc order
     * @param o
     * @return
     */
    @Override
    public int compareTo(ImNode o) {
        int weight1 = this.getBalance().get();
        int weight2 = o.getBalance().get();
        if(weight1 > weight2){
            return 1;
        }else if(weight2 > weight1){
            return -1;
        } 
        return 0;
    }
    
    public void incrementBalance(){
        this.balance.incrementAndGet();
    }
    
    public void decrementBalance(){
        this.balance.decrementAndGet();
    }
}
