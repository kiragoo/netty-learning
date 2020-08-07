package com.kirago.imClient.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("NettyClient")
public class NettyClient {
    
    @Value("${server.ip}")
    private String host;
    
    @Value("${server.ip}")
    private String ip;
}
