package com.kirago.netty.im.gateway.service.impl;


import com.kirago.netty.im.gateway.mybatis.entity.UserPO;
import com.kirago.netty.im.gateway.mybatis.mapper.UserMapper;
import com.kirago.netty.im.gateway.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserPO login(UserPO user) {
 /*       User sample = new User();
        sample.setUserName(user.getUserName());
        User u = userMapper.selectOne(sample);
        if (null == u) {
            log.info("找不到用户信息 username={}", user.getUserName());

            return null;

        }
*/
        //为了简化演示，去掉数据库的部分

        return user;
    }

    @Cacheable(value = "CrazyIMKey:User:", key = "#userid")
    public UserPO getById(String userid) {
        //为了简化演示，去掉数据库的部分


       /* User u = userMapper.selectByPrimaryKey(Integer.valueOf(userid));
        if (null == u) {
            log.info("找不到用户信息 userid={}", userid);
        }
        return u;
  */
        return null;
    }

    @CacheEvict(value = "CrazyIMKey:User:", key = "#userid")
    public int deleteById(String userid) {
        //为了简化演示，去掉数据库的部分


/*        int u = userMapper.deleteByPrimaryKey(Integer.valueOf(userid));
        if (0 == u) {
            log.info("找不到用户信息 userid={}", userid);
        }
        return u;*/
        return 0;
    }

}
