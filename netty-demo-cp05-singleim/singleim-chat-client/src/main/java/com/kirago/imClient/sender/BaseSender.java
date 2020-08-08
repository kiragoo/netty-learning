package com.kirago.imClient.sender;

import com.kirago.imClient.client.ClientSession;
import com.kirago.imCommon.common.bean.User;
import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public abstract class BaseSender {
    
    private User user;
    private ClientSession session;


    public boolean isConnected() {
        if (null == session) {
            log.info("session is null");
            return false;
        }

        return session.isConnected();
    }

    public boolean isLogin() {
        if (null == session) {
            log.info("session is null");
            return false;
        }

        return session.isLogin();
    }

    public void sendMsg(ProtoMsg3.Message message) {


        if (null == getSession() || !isConnected()) {
            log.info("连接还没成功");
            return;
        }

        Channel channel=getSession().getChannel();
        ChannelFuture f = channel.writeAndFlush(message);
        f.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future)
                    throws Exception {
                // 回调
                if (future.isSuccess()) {
                    sendSucced(message);
                } else{
                    sendfailed(message);

                }
            }

        });



/*

        try {
            f.sync();
        } catch (InterruptedException e) {

            e.printStackTrace();
            sendException(message);
        }

*/
    }

    protected void sendSucced(ProtoMsg3.Message message) {
        log.info("发送成功");

    }

    protected void sendfailed(ProtoMsg3.Message message) {
        log.info("发送失败");
    }

}
