package com.kirago.netty.im.server.handler;

import com.kirago.netty.im.common.concurrent.CallbackTask;
import com.kirago.netty.im.common.concurrent.CallbackTaskScheduler;
import com.kirago.netty.im.common.protocol.Proto3Msg;
import com.kirago.netty.im.server.processor.LoginProcessor;
import com.kirago.netty.im.server.session.LocalSession;
import com.kirago.netty.im.server.session.SessionManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {
    
    @Autowired
    LoginProcessor loginProcessor;

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

        //取得请求类型
        Proto3Msg.ProtoMsg.HeadType headType = pkg.getType();

        if (!headType.equals(loginProcessor.type())) {
            super.channelRead(ctx, msg);
            return;
        }


        LocalSession session = new LocalSession(ctx.channel());

        //异步任务，处理登录的逻辑
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                return   loginProcessor.action(session, pkg);
            }

            //异步任务返回
            @Override
            public void onBack(Boolean r) {
                if (r) {
                    ctx.pipeline().remove(LoginRequestHandler.this);
                    log.info("登录成功:" + session.getUser());

                } else {
                    SessionManager.inst().closeSession(ctx);

                    log.info("登录失败:" + session.getUser());

                }

            }
            //异步任务异常

            @Override
            public void onException(Throwable t) {
                SessionManager.inst().closeSession(ctx);

                log.info("登录失败:" + session.getUser());

            }
        });

    }
}
