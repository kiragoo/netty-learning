package com.kirago.netty.im.server.handler;

import com.google.gson.reflect.TypeToken;
import com.kirago.netty.im.common.constants.ServerConstants;
import com.kirago.netty.im.common.entity.ImNode;
import com.kirago.netty.im.common.entity.Notification;
import com.kirago.netty.im.common.protocol.Proto3Msg;
import com.kirago.netty.im.common.util.JsonUtil;
import com.kirago.netty.im.server.session.LocalSession;
import com.kirago.netty.im.server.session.RemoteSession;
import com.kirago.netty.im.server.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ChannelHandler.Sharable
public class RemoteNotificationHandler
        extends ChannelInboundHandlerAdapter {


    /**
     * 收到消息
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (!(msg instanceof Proto3Msg.ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        Proto3Msg.ProtoMsg.Message pkg = (Proto3Msg.ProtoMsg.Message) msg;

        //取得请求类型,如果不是通知消息，直接跳过
        Proto3Msg.ProtoMsg.HeadType headType = pkg.getType();

        if (!headType.equals(Proto3Msg.ProtoMsg.HeadType.MESSAGE_NOTIFICATION)) {
            super.channelRead(ctx, msg);
            return;
        }

        //处理消息的内容
        Proto3Msg.ProtoMsg.MessageNotification notificationPkg = pkg.getNotification();
        String json = notificationPkg.getJson();

        Notification<RemoteSession> notification =
                JsonUtil.jsonString2Object(json, new TypeToken<Notification<RemoteSession>>() {
                }.getType());


        //节点的链接成功
        if (notification.getType() == Notification.CONNECT_FINISHED) {

            Notification<ImNode> nodInfo =
                    JsonUtil.jsonString2Object(json, new TypeToken<Notification<ImNode>>() {
                    }.getType());


            log.info("收到分布式节点连接成功通知, node={}", json);

            ctx.pipeline().remove("loginRequest");
            ctx.channel().attr(ServerConstants.CHANNEL_NAME).set(JsonUtil.object2JsonString(nodInfo));
        }

        //上线的通知
        if (notification.getType() == Notification.SESSION_ON) {
            log.info("收到用户上线通知, node={}", json);
            RemoteSession remoteSession = notification.getData();
            SessionManager.inst().addRemoteSession(remoteSession);
        }

        //下线的通知
        if (notification.getType() == Notification.SESSION_OFF) {
            log.info("收到用户下线通知, node={}", json);
            RemoteSession remoteSession = notification.getData();
            SessionManager.inst().removeRemoteSession(remoteSession.getSessionId());
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx)
            throws Exception {
        LocalSession session = LocalSession.getSession(ctx);

        if (null != session) {

            session.unbind();

        }
    }
}
