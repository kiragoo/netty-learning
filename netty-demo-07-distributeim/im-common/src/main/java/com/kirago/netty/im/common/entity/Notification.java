package com.kirago.netty.im.common.entity;


import lombok.Data;

@Data
public class Notification<T> {

    /**
     * the notification of login in
     */
    public static final int SESSION_ON = 10;

    /**
     * the notification of login out
     */
    public static final int SESSION_OFF = 20;

    /**
     * the notification of connection success
     */
    public static final int CONNECT_FINISHED = 30;
    
    private int type;
    private T data;

    public Notification() {
    }

    public Notification(T t) {
        data = t;
    }
}
