package com.kirago.netty.im.common.util;

import java.util.UUID;

public class UuidUti {
    
    public static String getUuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
