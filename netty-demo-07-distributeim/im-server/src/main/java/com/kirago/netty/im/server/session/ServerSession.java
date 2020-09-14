package com.kirago.netty.im.server.session;

public interface ServerSession {
    
    void writeAndFlush(Object pkg);
    
    String getSessionId();
    
    boolean isValid();
}
