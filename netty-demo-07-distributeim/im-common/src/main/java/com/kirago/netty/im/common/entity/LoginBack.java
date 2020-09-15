package com.kirago.netty.im.common.entity;

import com.kirago.netty.im.common.entity.DTO.UserDTO;
import lombok.Data;

@Data
public class LoginBack {
    
    private ImNode imNode;
    
    private String token;
    
    private UserDTO userDTO;
}
