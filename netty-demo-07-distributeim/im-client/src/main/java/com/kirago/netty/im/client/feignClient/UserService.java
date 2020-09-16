package com.kirago.netty.im.client.feignClient;

import com.kirago.netty.im.common.entity.DTO.UserDTO;
import com.kirago.netty.im.common.entity.PT.UserPT;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "userService", url = "${remote.server}:${remote.port}/user")
public interface UserService {
    
    @PostMapping("/login")
    UserPT login(@RequestBody UserDTO userDTO);
}
