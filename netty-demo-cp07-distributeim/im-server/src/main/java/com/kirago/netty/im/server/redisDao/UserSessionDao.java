package com.kirago.netty.im.server.redisDao;

import com.kirago.netty.im.server.session.UserSession;

public interface UserSessionDao {
    
    void save(UserSession userSession);
    
    UserSession get(String userId);

    void cacheUser(String userId, String sessionId);

    void removeUserSession(String userId, String sessionId);
    
    UserSession getAllSession(String userId);
}
