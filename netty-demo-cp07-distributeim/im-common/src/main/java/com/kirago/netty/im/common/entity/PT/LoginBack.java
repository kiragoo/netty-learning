package com.kirago.netty.im.common.entity.PT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginBack {
    
    private ImNode imNode;
    
    private String token;
    
    private UserPT userPT;
}
