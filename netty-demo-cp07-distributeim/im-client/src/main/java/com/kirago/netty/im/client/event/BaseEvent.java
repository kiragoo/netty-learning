package com.kirago.netty.im.client.event;

public interface BaseEvent<T> {
    
    void exec(T t);
    
    String getKey();
    
    String getTip();
    
}
