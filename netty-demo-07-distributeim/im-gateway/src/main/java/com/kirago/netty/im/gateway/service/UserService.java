package com.kirago.netty.im.gateway.service;


import com.kirago.netty.im.common.entity.DTO.UserDTO;
import com.kirago.netty.im.common.entity.PT.LoginBack;
import com.kirago.netty.im.common.entity.PT.UserPT;
import com.kirago.netty.im.gateway.entity.DO.UserDO;

public interface UserService {

//    UserDO login(UserPO user);
//
//    UserPO getById(String userid);
//
//    int deleteById(String userid);
    void register(UserDTO userDTO);
    
    UserPT login(UserDTO userDTO);
    


}
