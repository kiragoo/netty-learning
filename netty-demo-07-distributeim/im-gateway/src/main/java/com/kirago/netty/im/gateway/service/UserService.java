package com.kirago.netty.im.gateway.service;


import com.kirago.netty.im.gateway.mybatis.entity.UserPO;

public interface UserService {

    UserPO login(UserPO user);

    UserPO getById(String userid);

    int deleteById(String userid);


}
