package com.kirago.netty.im.client.ClientSender;


import com.kirago.netty.im.client.event.BaseEvent;
import com.kirago.netty.im.client.client.ClientSession;
import com.kirago.netty.im.common.concurrent.CallbackTask;
import com.kirago.netty.im.common.concurrent.CallbackTaskScheduler;
import com.kirago.netty.im.common.entity.PT.UserPT;
import com.kirago.netty.im.common.protocol.Proto3Msg;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@Slf4j
public abstract class BaseSender {


    private UserPT userPT;
    private ClientSession session;

    @Autowired
    protected BaseEvent baseEvent;


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

    public void sendMsg(Proto3Msg.ProtoMsg.Message message) {


        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                if (null == getSession()) {
                    throw new Exception("session is null");
                }

                if (!isConnected()) {
                    log.info("连接还没成功");
                    throw new Exception("连接还没成功");
                }

                final Boolean[] isSuccess = {false};

                ChannelFuture f = getSession().witeAndFlush(message);
                f.addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future)
                            throws Exception {
                        // 回调
                        if (future.isSuccess()) {
                            isSuccess[0] = true;
                        }
                    }

                });


                try {
                    f.sync();
                } catch (InterruptedException e) {
                    isSuccess[0] = false;
                    e.printStackTrace();
                    throw new Exception("error occur");
                }

                return isSuccess[0];
            }

            @Override
            public void onBack(Boolean b) {
                if (b) {
                    BaseSender.this.sendSucced(message);

                } else {
                    BaseSender.this.sendFailed(message);

                }

            }

            @Override
            public void onException(Throwable t) {
                BaseSender.this.sendException(message);


            }
        });
    }

    protected void sendSucced(Proto3Msg.ProtoMsg.Message message) {
        log.info("发送成功");

    }

    protected void sendFailed(Proto3Msg.ProtoMsg.Message message) {
        log.info("发送失败");
    }

    protected void sendException(Proto3Msg.ProtoMsg.Message message) {
        log.info("发送消息出现异常");

    }

}
