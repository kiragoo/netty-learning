package com.kirago.netty.im.client.client;

import com.kirago.netty.im.client.ClientSender.ChatSender;
import com.kirago.netty.im.client.ClientSender.LoginSender;
import com.kirago.netty.im.client.event.BaseEvent;
import com.kirago.netty.im.client.event.ChatEvent;
import com.kirago.netty.im.client.event.LoginEvent;
import com.kirago.netty.im.client.feignClient.WebOperator;
import com.kirago.netty.im.common.concurrent.FutureTaskScheduler;
import com.kirago.netty.im.common.entity.DTO.UserDTO;
import com.kirago.netty.im.common.entity.PT.ImNode;
import com.kirago.netty.im.common.entity.PT.LoginBack;
import com.kirago.netty.im.common.util.JsonUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
* @description:    消息传输
* @author:         kirago
* @date:     2020/9/20 5:38 下午
* @updateRemark:   修改内容
* @version:        1.0
*/

@Slf4j
@Service
@Data
public class EventController {
    
    private Map<String, BaseEvent> eventMap;
    
    private ClientSession clientSession;
    
    @Autowired
    private NettyClient nettyClient;

    @Autowired
    private LoginEvent loginEvent;

    @Autowired
    private LoginSender loginSender;
    
    @Autowired
    private ChatEvent chatEvent;
    
    @Autowired
    private ChatSender chatSender;
    
    private Channel channel;
    
    private boolean connectFlag = false;
    
    private UserPT userPT;

    GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) ->
    {


        log.info(new Date() + ": 连接已经断开……");
        channel = f.channel();

        // 创建会话
        ClientSession session =
                channel.attr(ClientSession.SESSION_KEY).get();
        session.close();

        //唤醒用户线程
        notifyCommandThread();
    };


    GenericFutureListener<ChannelFuture> connectedListener = (ChannelFuture f) ->
    {
        final EventLoop eventLoop
                = f.channel().eventLoop();
        if (!f.isSuccess()) {
            log.info("[连接失败!在10s之后准备尝试重连!]");
            eventLoop.schedule(
                    () -> nettyClient.doConnect(),
                    10,
                    TimeUnit.SECONDS);

            connectFlag = false;
        } else {
            connectFlag = true;

            log.info("[IM 服务器 连接成功!]");
            channel = f.channel();

            // 创建会话
            clientSession = new ClientSession(channel);
            clientSession.setConnected(true);
            channel.closeFuture().addListener(closeListener);

            //唤醒用户线程
            notifyCommandThread();
        }

    };
    
    public void initEventMap() {
        eventMap = new HashMap<>();
        eventMap.put(loginEvent.getKey(), loginEvent);
        // TODO: 2020/9/22 添加其他事件 

//        Set<Map.Entry<String, BaseEvent>> entrys =
//                eventMap.entrySet();
//        Iterator<Map.Entry<String, BaseEvent>> iterator =
//                entrys.iterator();
        
    }


    public void startConnectServer() {

        FutureTaskScheduler.add(() ->
        {
            nettyClient.setConnectedListener(connectedListener);
            nettyClient.doConnect();
        });
    }


    public synchronized void notifyCommandThread() {
        //唤醒，命令收集程
        this.notify();

    }

    public synchronized void waitCommandThread() {

        //休眠，命令收集线程
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    /**
     * 开始连接服务器
     */
    private void userLoginAndConnectToServer() {

        //登录
        if (isConnectFlag()) {
            log.info("已经登录成功，不需要重复登录");
            return;
        }
        LoginEvent loginEvent = (LoginEvent) eventMap.get(LoginConsoleCommand.KEY);
        
        // TODO: 2020/9/22 此处用户如何传递需要完善 
        
        UserDTO userDTO = new UserDTO();
        loginEvent.exec(userDTO);

        log.info("开始登录WEB GATE ------>");
        LoginBack webBack = WebOperator.login(userDTO);
        ImNode node = webBack.getImNode();
        log.info("WEB GATE 返回的node节点是：{}", JsonUtil.object2JsonString(node));

        log.info("step2：开始连接Netty 服务节点");

        nettyClient.setConnectedListener(connectedListener);
        nettyClient.setHost(node.getHost());
        nettyClient.setPort(node.getPort());
        nettyClient.doConnect();
        waitCommandThread();
        log.info("step2：Netty 服务节点连接成功");


        log.info("step3：开始登录Netty 服务节点");

        this.userPT = userPT;
        clientSession.setUser(userPT);
        loginSender.setUserPT(userPT);
        loginSender.setSession(clientSession);
        loginSender.sendLoginMsg();
        waitCommandThread();

        connectFlag =true;
    }

    public void startEventThread() throws InterruptedException {

        while (true) {
            //建立连接
            while (!connectFlag) {
                //输入用户名，然后登录
                userLoginAndConnectToServer();
            }
            //处理命令
            while (null != clientSession) {
                BaseEvent event = (ChatEvent) eventMap.get(ChatEvent.KEY);
                chatEvent.exec(scanner);
                startOneChat(chatEvent);

            /*    clientCommandMenu.exec(scanner);
                String key = clientCommandMenu.getCommandInput();
                BaseCommand command = commandMap.get(key);

                if (null == command) {
                    System.err.println("无法识别[" + command + "]指令，请重新输入!");
                    continue;
                }


                switch (key) {
                    case ChatConsoleCommand.KEY:
                        command.exec(scanner);
                        startOneChat((ChatConsoleCommand) command);
                        break;



                    case LogoutConsoleCommand.KEY:
                        command.exec(scanner);
                        startLogout(command);
                        break;

                }*/
            }
        }
    }

    //发送单聊消息
    private void startOneChat(ChatEvent chatEvent) {
        //登录
        if (!isLogin()) {
            log.info("还没有登录，请先登录");
            return;
        }
        chatSender.setSession(clientSession);
        chatSender.setUserPT(userPT);
        chatSender.sendChatMsg(chatEvent.(), c.getMessage());
    }




//    private void startLogout(BaseCommand command) {
//        //登出
//        if (!isLogin()) {
//            log.info("还没有登录，请先登录");
//            return;
//        }
//        //todo 登出
//    }

    public boolean isLogin() {
        if (null == clientSession) {
            log.info("session is null");
            return false;
        }
        return clientSession.isLogin();
    }

}
