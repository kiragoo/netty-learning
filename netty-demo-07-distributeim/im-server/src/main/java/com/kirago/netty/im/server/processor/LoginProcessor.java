package com.kirago.netty.im.server.processor;

import com.kirago.netty.im.common.constants.ProtoInstant;
import com.kirago.netty.im.common.entity.PT.UserPT;
import com.kirago.netty.im.common.protocol.Proto3Msg;
import com.kirago.netty.im.server.protoBuilder.LoginResponseBuilder;
import com.kirago.netty.im.server.session.LocalSession;
import com.kirago.netty.im.server.session.ServerSession;
import com.kirago.netty.im.server.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class LoginProcessor extends AbstractServerProcessor {
    
    @Autowired
    LoginResponseBuilder loginResponseBuilder;
    
    @Autowired
    SessionManager sessionManager;

    @Override
    public Proto3Msg.ProtoMsg.HeadType type() {
        return Proto3Msg.ProtoMsg.HeadType.LOGIN_REQUEST;
    }

    @Override
    public Boolean action(LocalSession session,
                          Proto3Msg.ProtoMsg.Message proto) {
        // 取出token验证
        Proto3Msg.ProtoMsg.LoginRequest info = proto.getLoginRequest();
        long seqNo = proto.getSequence();

        UserPT user = UserPT.fromMsg(info);

        //检查用户
        boolean isValidUser = checkUser(user);
        if (!isValidUser) {
            ProtoInstant.ResultCodeEnum resultcode =
                    ProtoInstant.ResultCodeEnum.NO_TOKEN;
            Proto3Msg.ProtoMsg.Message response =
                    loginResponseBuilder.loginResponse(resultcode, seqNo, "-1");
            //发送之后，断开连接
            session.writeAndClose(response);
            return false;
        }

        session.setUser(user);

        /**
         * 绑定session
         */
        session.bind();
        sessionManager.addLocalSession(session);


        /**
         * 通知客户端：登录成功
         */

        ProtoInstant.ResultCodeEnum resultcode = ProtoInstant.ResultCodeEnum.SUCCESS;
        Proto3Msg.ProtoMsg.Message response =
                loginResponseBuilder.loginResponse(resultcode, seqNo, session.getSessionId());
        session.writeAndFlush(response);
        return true;
    }

    private boolean checkUser(UserPT user) {

        //校验用户,比较耗时的操作,需要100 ms以上的时间
        //方法1：调用远程用户restfull 校验服务
        //方法2：调用数据库接口校验

        List<ServerSession> l = sessionManager.getSessionsBy(user.getUserId());


        if (null != l && l.size() > 0) {
            return false;
        }

        return true;

    }
}
