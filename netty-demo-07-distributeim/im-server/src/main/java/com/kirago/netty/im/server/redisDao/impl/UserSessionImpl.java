package com.kirago.netty.im.server.redisDao.impl;

import com.kirago.netty.im.common.util.JsonUtil;
import com.kirago.netty.im.server.distributed.ImWorker;
import com.kirago.netty.im.server.redisDao.UserSessionDao;
import com.kirago.netty.im.server.session.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserSessionImpl implements UserSessionDao {
    
    public static final String REDIS_USER_SESSION_PREFIX = "UserSessions:uid:";
    
    private static final long CACHE_LONG = 60 * 4;
    
    @Autowired
    private RedisTemplate<Serializable, Serializable> redisTemplate;

    @Override
    public void save(final UserSession us) {
        String key = REDIS_USER_SESSION_PREFIX + us.getUserId();
        String value = JsonUtil.object2JsonString(us);
        redisTemplate.opsForValue().set(key, value, CACHE_LONG, TimeUnit.MINUTES);
    }
    
    @Override
    public UserSession get(final String userId){
        String key = REDIS_USER_SESSION_PREFIX + userId;
        String value = (String) redisTemplate.opsForValue().get(key);
        if(!StringUtils.isEmpty(value)){
            return JsonUtil.jsonString2Object(value, UserSession.class);
        }
        log.info(" UserSession 为空");
        return null;
    }

    @Override
    public void cacheUser(String uid, String sessionId) {
        UserSession us = get(uid);
        if (null == us) {
            us = new UserSession(uid);
        }
        us.addSession(sessionId, ImWorker.getInst().getLocalNodeInfo());
        save(us);
    }

    @Override
    public void removeUserSession(String uid, String sessionId) {
        UserSession us = get(uid);
        if (null == us) {
            us = new UserSession(uid);
        }
        us.removeSession(sessionId);
        save(us);
    }

    @Override
    public UserSession getAllSession(String userId) {
        UserSession us = get(userId);
        if (null != us) {
            return us;
        }
        return null;
    }

}
