package com.kirago.netty.im.common.constants;

import io.netty.util.AttributeKey;

public class ServerConstants {
    /**
     * parent path of worker node 
     */
    public static final String WORKER_PARENT_PATH = "/im/nodes";

    /**
     * prefix of the worker path
     */
    public static final String WORKER_PREFIX_PATH = WORKER_PARENT_PATH + "/seq-";

    public static final String WEB_URL = "http://127.0.0.1:38080";
    
    /**
     * the numbers of account online
     */
    public static final String COUNTER_PATH = "/im/OnlineCounter";

    public static final AttributeKey<String> CHANNEL_NAME=
            AttributeKey.valueOf("CHANNEL_NAME");

}
