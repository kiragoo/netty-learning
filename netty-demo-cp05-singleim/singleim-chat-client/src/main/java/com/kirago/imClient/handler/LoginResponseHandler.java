package com.kirago.imClient.handler;

import com.kirago.imClient.client.ClientSession;
import com.kirago.imCommon.common.ProtoInstant;
import com.kirago.imCommon.common.bean.msg.ProtoMsg3;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @Description:    登录请求处理器 
* @Author:         kirago
* @CreateDate:     2020/8/8 10:07 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
@Slf4j
@Service("LoginResponseHandler")
@ChannelHandler.Sharable
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {


    /**
    * @Description: 业务处理逻辑
    * @Param: 
    * @return: 
    **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        //判断消息实例
        if (null == msg || !(msg instanceof ProtoMsg3.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        //判断类型
        ProtoMsg3.Message pkg = (ProtoMsg3.Message) msg;
        ProtoMsg3.HeadType headType = ((ProtoMsg3.Message) msg).getType();
        if (!headType.equals(ProtoMsg3.HeadType.LOGIN_RESPONSE)) {
            super.channelRead(ctx, msg);
            return;
        }


        //判断返回是否成功
        ProtoMsg3.LoginResponse info = pkg.getLoginResponse();

        ProtoInstant.ResultCodeEnum result =
                ProtoInstant.ResultCodeEnum.values()[info.getCode()];

        if (!result.equals(ProtoInstant.ResultCodeEnum.SUCCESS)) {
            //登录失败
            log.info(result.getDesc());
        } else {
            //登录成功
            ClientSession.loginSuccess(ctx, pkg);
            ChannelPipeline p = ctx.pipeline();
            //移除登录响应处理器
            p.remove(this);

            //在编码器后面，动态插入心跳处理器
            p.addAfter("encoder", "heartbeat", new HeartBeatClientHandler());
        }

    }
}
