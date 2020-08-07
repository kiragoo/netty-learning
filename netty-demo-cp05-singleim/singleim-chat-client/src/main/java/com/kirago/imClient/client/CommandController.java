package com.kirago.imClient.client;

import com.kirago.imClient.command.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service("CommandController")
@Data
public class CommandController {
    
    @Autowired
    private LoginConsoleCommand loginConsoleCommand;
    
    @Autowired
    private LogoutConsoleCommand logoutConsoleCommand;
    
    @Autowired
    private ChatConsoleCommand chatConsoleCommand;
    
    @Autowired
    private ClientCommandMenu clientCommandMenu;
    
    private Map<String, BaseCommand> commandMap;

    private String menuString;

    //会话类
    private ClientSession session;


}
