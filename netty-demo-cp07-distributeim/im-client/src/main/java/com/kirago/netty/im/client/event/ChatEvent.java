package com.kirago.netty.im.client.event;

import com.kirago.netty.im.common.entity.PT.ChatMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatEvent implements BaseEvent<ChatMsg> {
    
    public static final String KEY = "2";
    
    private ChatMsg chatMsg;
    
    @Override
    public void exec(ChatMsg chatMsg){
        // TODO: 2020/9/20 聊天DTO 
        this.chatMsg = chatMsg;

        
        log.info("发送的目标用户: [{}], 发送内容: [{}]", chatMsg.getTo(), "todo");
    }
    
    @Override
    public String getKey(){
        return KEY;
    }
    
    @Override
    public String getTip(){
        return "聊天";
    }
}
