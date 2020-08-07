package com.kirago.imServer.handler;

import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import com.kirago.imCommon.concurrent.CallbackTask;
import com.kirago.imCommon.concurrent.CallbackTaskScheduler;
import com.kirago.imServer.processor.LoginProcessor;
import com.kirago.imServer.server.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("LoginRequestHandler")
@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    LoginProcessor loginProcesser;

    /**
     * 收到消息
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (null == msg
                || !(msg instanceof ProtoMsg3.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ProtoMsg3.Message pkg = (ProtoMsg3.Message) msg;

        //取得请求类型
        ProtoMsg3.HeadType headType = pkg.getType();

        if (!headType.equals(loginProcesser.type())) {
            super.channelRead(ctx, msg);
            return;
        }


        ServerSession session = new ServerSession(ctx.channel());

        //异步任务，处理登录的逻辑
        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                boolean r = loginProcesser.action(session, pkg);
                return r;
            }

            //异步任务返回
            @Override
            public void onBack(Boolean r) {
                if (r) {
                    ctx.pipeline().remove(LoginRequestHandler.this);
                    log.info("登录成功:" + session.getUser());

                } else {
                    ServerSession.closeSession(ctx);
                    log.info("登录失败:" + session.getUser());

                }

            }
            //异步任务异常

            @Override
            public void onException(Throwable t) {
                ServerSession.closeSession(ctx);
                log.info("登录失败:" + session.getUser());

            }
        });

    }


}

