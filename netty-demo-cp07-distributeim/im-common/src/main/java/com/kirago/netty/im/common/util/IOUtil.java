package com.kirago.netty.im.common.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class IOUtil {
    
    public static String getHostAddress(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(" 获取本地地址失败: {}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
