package com.kirago.netty.im.server.session;

import com.kirago.netty.im.common.entity.ImNode;
import com.kirago.netty.im.server.distributed.ImWorker;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class UserSession {
    
    private String userId;
    
    private Map<String, ImNode> map = new LinkedHashMap<>(10);
    
    public UserSession(String userId){
        this.userId = userId;
    }
    
    public void addSession(String sessionId, ImNode imNode){
        map.put(sessionId, imNode);
    }
    
    public void removeSession(String sessionId){
        map.remove(sessionId);
    }
    
    public void addLocalSession(LocalSession localSession){
        map.put(localSession.getSessionId(), ImWorker.getInst().getLocalNodeInfo());
    }
}
