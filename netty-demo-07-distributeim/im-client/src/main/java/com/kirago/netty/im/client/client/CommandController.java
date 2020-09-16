package com.kirago.netty.im.client.client;


import com.kirago.netty.im.client.ClientSender.ChatSender;
import com.kirago.netty.im.client.ClientSender.LoginSender;
import com.kirago.netty.im.client.clientCommand.*;
import com.kirago.netty.im.client.feignClient.WebOperator;
import com.kirago.netty.im.common.concurrent.FutureTaskScheduler;
import com.kirago.netty.im.common.entity.DTO.UserDTO;
import com.kirago.netty.im.common.entity.PT.UserPT;
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

@Slf4j
@Data
@Service
public class CommandController {

    //聊天命令收集类
    @Autowired
    ChatConsoleCommand chatConsoleCommand;

    //登录命令收集类
    @Autowired
    LoginConsoleCommand loginConsoleCommand;

    //登出命令收集类
    @Autowired
    LogoutConsoleCommand logoutConsoleCommand;

    //菜单命令收集类
    @Autowired
    ClientCommandMenu clientCommandMenu;

    private Map<String, BaseCommand> commandMap;

    private String menuString;

    //会话类
    private ClientSession session;


    @Autowired
    private NettyClient nettyClient;

    private Channel channel;

    @Autowired
    private ChatSender chatSender;

    @Autowired
    private LoginSender loginSender;


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
            log.info("连接失败!在10s之后准备尝试重连!");
            eventLoop.schedule(
                    () -> nettyClient.doConnect(),
                    10,
                    TimeUnit.SECONDS);

            connectFlag = false;
        } else {
            connectFlag = true;

            log.info("疯狂创客圈 IM 服务器 连接成功!");
            channel = f.channel();

            // 创建会话
            session = new ClientSession(channel);
            session.setConnected(true);
            channel.closeFuture().addListener(closeListener);

            //唤醒用户线程
            notifyCommandThread();
        }

    };
    private Scanner scanner;


    public void initCommandMap() {
        commandMap = new HashMap<>();
        commandMap.put(clientCommandMenu.getKey(), clientCommandMenu);
        commandMap.put(chatConsoleCommand.getKey(), chatConsoleCommand);
        commandMap.put(loginConsoleCommand.getKey(), loginConsoleCommand);
        commandMap.put(logoutConsoleCommand.getKey(), logoutConsoleCommand);

        Set<Map.Entry<String, BaseCommand>> entrys =
                commandMap.entrySet();
        Iterator<Map.Entry<String, BaseCommand>> iterator =
                entrys.iterator();

        StringBuilder menus = new StringBuilder();
        menus.append("[menu] ");
        while (iterator.hasNext()) {
            BaseCommand next = iterator.next().getValue();

            menus.append(next.getKey())
                    .append("->")
                    .append(next.getTip())
                    .append(" | ");

        }
        menuString = menus.toString();

        clientCommandMenu.setAllCommandsShow(menuString);


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
        LoginConsoleCommand command = (LoginConsoleCommand) commandMap.get(LoginConsoleCommand.KEY);
        command.exec(scanner);



        UserDTO userDTO = new UserDTO();

        log.info("step1：开始登录WEB GATE");
        LoginBack webBack = WebOperator.login(userDTO);
        ImNode node = webBack.getImNode();
        log.info("step1 WEB GATE 返回的node节点是：{}", JsonUtil.object2JsonString(node));

        log.info("step2：开始连接Netty 服务节点");

        nettyClient.setConnectedListener(connectedListener);
        nettyClient.setHost(node.getHost());
        nettyClient.setPort(node.getPort());
        nettyClient.doConnect();
        waitCommandThread();
        log.info("step2：Netty 服务节点连接成功");


        log.info("step3：开始登录Netty 服务节点");

        this.userPT = userPT;
        session.setUser(userPT);
        loginSender.setUserPT(userPT);
        loginSender.setSession(session);
        loginSender.sendLoginMsg();
        waitCommandThread();

        connectFlag =true;
    }

    public void startCommandThread() throws InterruptedException {
        scanner = new Scanner(System.in);
        Thread.currentThread().setName("命令线程");

        while (true) {
            //建立连接
            while (connectFlag == false) {
                //输入用户名，然后登录
                userLoginAndConnectToServer();
            }
            //处理命令
            while (null != session) {
                ChatConsoleCommand command = (ChatConsoleCommand) commandMap.get(ChatConsoleCommand.KEY);
                command.exec(scanner);
                startOneChat(command);

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
    private void startOneChat(ChatConsoleCommand c) {
        //登录
        if (!isLogin()) {
            log.info("还没有登录，请先登录");
            return;
        }
        chatSender.setSession(session);
        chatSender.setUserPT(userPT);
        chatSender.sendChatMsg(c.getToUserId(), c.getMessage());


    }




    private void startLogout(BaseCommand command) {
        //登出
        if (!isLogin()) {
            log.info("还没有登录，请先登录");
            return;
        }
        //todo 登出
    }


    public boolean isLogin() {
        if (null == session) {
            log.info("session is null");
            return false;
        }

        return session.isLogin();
    }

}
