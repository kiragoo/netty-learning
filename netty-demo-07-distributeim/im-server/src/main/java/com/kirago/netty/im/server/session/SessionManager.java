package com.kirago.netty.im.server.session;


import com.kirago.netty.im.common.entity.DTO.UserDTO;
import com.kirago.netty.im.common.entity.ImNode;
import com.kirago.netty.im.common.entity.Notification;
import com.kirago.netty.im.common.util.JsonUtil;
import com.kirago.netty.im.server.distributed.ImWorker;
import com.kirago.netty.im.server.distributed.OnlineCounter;
import com.kirago.netty.im.server.distributed.PeerManager;
import com.kirago.netty.im.server.redisDao.UserSessionDao;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class SessionManager {


    @Autowired
    UserSessionDao userSessionDAO;


    private static SessionManager singleInstance = null;

    /*本地会话集合*/

    private ConcurrentHashMap<String, LocalSession> localSessionMap = new ConcurrentHashMap();


    /*远程会话集合*/

    private ConcurrentHashMap<String, RemoteSession> remoteSessionMap = new ConcurrentHashMap();

    /*本地用户集合*/
    private ConcurrentHashMap<String, UserSession> sessionsLocalCache = new ConcurrentHashMap();


    public static SessionManager inst() {
        return singleInstance;
    }

    public static void setSingleInstance(SessionManager singleInstance) {
        SessionManager.singleInstance = singleInstance;
    }

    /**
     * 登录成功之后， 增加session对象
     */
    public void addLocalSession(LocalSession session) {
        String sessionId = session.getSessionId();
        localSessionMap.put(sessionId, session);


        String uid = session.getUser().getUserId();

        //增加用户数
        OnlineCounter.getInst().increment();
        log.info("本地session增加：{},  在线总数:{} ",
                JsonUtil.object2JsonString(session.getUser()),
                OnlineCounter.getInst().getCurValue());
        ImWorker.getInst().incBalance();


        //增加用户的session 信息到缓存
        userSessionDAO.cacheUser(uid, sessionId);


        /**
         * 通知其他节点
         */
        notifyOtherImNode(session, Notification.SESSION_ON);

    }


    /**
     * 删除session
     */
    public void removeLocalSession(String sessionId) {
        if (!localSessionMap.containsKey(sessionId)) {
            return;
        }
        LocalSession session = localSessionMap.get(sessionId);
        String uid = session.getUser().getUserId();
        localSessionMap.remove(sessionId);


        //减少用户数
        OnlineCounter.getInst().decrement();
        log.info("本地session减少：{},  在线总数:{} ",
                JsonUtil.object2JsonString(session.getUser()),
                OnlineCounter.getInst().getCurValue());
        ImWorker.getInst().decrBalance();

        //分布式保存user和所有session
        userSessionDAO.removeUserSession(uid, sessionId);


        /**
         * 通知其他节点
         */
        notifyOtherImNode(session, Notification.SESSION_OFF);

    }

    /**
     * 通知其他节点
     *
     * @param session session
     * @param type    类型
     */
    private void notifyOtherImNode(LocalSession session, int type) {
        UserDTO user = session.getUser();
        RemoteSession remoteSession = RemoteSession.builder()
                .sessionId(session.getSessionId())
                .imNode(PeerManager.getInst().getLocalNode())
                .userId(user.getUserId())
                .valid(true)
                .build();
        Notification<RemoteSession> notification = new Notification<RemoteSession>(remoteSession);
        notification.setType(type);
        PeerManager.getInst().sendNotification(JsonUtil.object2JsonString(notification));
    }


    /**
     * 根据用户id，获取session对象
     */
    public List<ServerSession> getSessionsBy(String userId) {

        List<ServerSession> sessions = new LinkedList<>();
        UserSession userSessions = loadFromCache(userId);

        if (null == userSessions) {
            return null;
        }
        Map<String, ImNode> allSession = userSessions.getMap();

        allSession.keySet().stream().forEach(key -> {

            //首先取得本地的session
            ServerSession session = localSessionMap.get(key);

            //没有命中，取得远程的session
            if (session == null) {
                session = remoteSessionMap.get(key);

            }
            sessions.add(session);
        });


        return sessions;

    }

    /**
     * 从二级缓存加载
     *
     * @param userId 用户的id
     * @return 用户的集合
     */
    private UserSession loadFromRedis(String userId) {


        UserSession userSession = userSessionDAO.getAllSession(userId);

        if (null == userSession) {
            return null;
        }
        Map<String, ImNode> map = userSession.getMap();
        map.keySet().stream().forEach(key -> {
            ImNode node = map.get(key);
            //当前节点直接忽略
            if (!node.equals(ImWorker.getInst().getLocalNodeInfo())) {

                remoteSessionMap.put(key, new RemoteSession(key, userId, node));

            }
        });


        return userSession;
    }


    /**
     * 从二级缓存加载
     *
     * @param userId 用户的id
     * @return 用户的集合
     */
    private UserSession loadFromCache(String userId) {

        //本地缓存
        UserSession userSession = sessionsLocalCache.get(userId);

        if (null != userSession
                && null != userSession.getMap()
                && userSession.getMap().keySet().size() > 0) {
            return userSession;
        }


        UserSession finalUserSession = new UserSession(userId);
        localSessionMap.values().stream().forEach(session -> {

            if (userId.equals(session.getUser().getUserId())) {
                finalUserSession.addLocalSession(session);
            }
        });

        remoteSessionMap.values().stream().forEach(session -> {

            if (userId.equals(session.getUserId())) {
                finalUserSession.addSession(session.getSessionId(), session.getImNode());
            }
        });

        sessionsLocalCache.put(userId, finalUserSession);


        return finalUserSession;
    }


    /**
     * 增加 远程的 session
     */
    public void addRemoteSession(RemoteSession remoteSession) {
        String sessionId = remoteSession.getSessionId();
        if (localSessionMap.containsKey(sessionId)) {
            log.error("通知有误，通知到了会话所在的节点");
            return;
        }

        remoteSessionMap.put(sessionId, remoteSession);
        //删除本地保存的 远程session
        String uid = remoteSession.getUserId();
        UserSession session = sessionsLocalCache.get(uid);
        if (null == session) {
            session = new UserSession(uid);
            sessionsLocalCache.put(uid, session);
        }

        session.addSession(sessionId, remoteSession.getImNode());
    }

    /**
     * 删除 远程的 session
     */
    public void removeRemoteSession(String sessionId) {
        if (localSessionMap.containsKey(sessionId)) {
            log.error("通知有误，通知到了会话所在的节点");
            return;
        }

        RemoteSession s = remoteSessionMap.get(sessionId);
        remoteSessionMap.remove(sessionId);

        //删除本地保存的 远程session
        String uid = s.getUserId();
        UserSession session = sessionsLocalCache.get(uid);
        session.removeSession(sessionId);

    }


    //关闭连接
    public void closeSession(ChannelHandlerContext ctx) {

        LocalSession session =
                ctx.channel().attr(LocalSession.SESSION_KEY).get();

        if (null != session && session.isValid()) {
            session.close();
            this.removeLocalSession(session.getSessionId());
        }
    }


}
