package com.kirago.netty.im.common.entity.DTO;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(value = "用户注册")
@Data
public class UserDTO {
    
    private String username;
    
    private String password;
}
