package com.kirago.netty.im.gateway.service.impl;


import com.kirago.netty.im.common.entity.DTO.UserDTO;
import com.kirago.netty.im.common.entity.PT.UserPT;
import com.kirago.netty.im.common.util.DateUtil;
import com.kirago.netty.im.common.util.JwtUtil;
import com.kirago.netty.im.common.util.MapperUtil;
import com.kirago.netty.im.common.util.UuidUti;
import com.kirago.netty.im.gateway.entity.DO.UserDO;
import com.kirago.netty.im.gateway.mapper.UserDOMapper;
import com.kirago.netty.im.gateway.service.UserService;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;
    
    @Override
    public void register(UserDTO userDTO){
        MapperUtil.classMap(UserDTO.class, UserDO.class).byDefault().register();

        MapperFacade mapperFacade = MapperUtil.getMapperFactory().getMapperFacade();
        
        UserDO userDO = mapperFacade.map(userDTO, UserDO.class);
        userDO.setUserId(UuidUti.getUuid());
        userDO.setRegisterAt(DateUtil.getCurrentDate());
        userDO.setToken(JwtUtil.createToken());
        userDOMapper.insert(userDO);
    }

    @Override
    public UserPT login(UserDTO userDTO){
        String username = userDTO.getUsername();
        if(StringUtils.isNotEmpty(username) && StringUtils.isNotBlank(username)){
            UserDO userDO =  userDOMapper.selectByUsername(username);
            MapperUtil.classMap(UserDO.class, UserPT.class).byDefault().register();
            
            MapperFacade mapperFacade = MapperUtil.getMapperFactory().getMapperFacade();
            UserPT userPT = mapperFacade.map(userDO, UserPT.class);
            // TODO: 2020/9/16 调用 sessionUti 工具生成 客户端 sessionId
            userPT.setSessionId(userDO.getUsername());
            return userPT;
        }
        return null;
    }
//    @Override
//    public UserPO login(UserPO user) {
// /*       User sample = new User();
//        sample.setUserName(user.getUserName());
//        User u = userMapper.selectOne(sample);
//        if (null == u) {
//            log.info("找不到用户信息 username={}", user.getUserName());
//
//            return null;
//
//        }
//*/
//        //为了简化演示，去掉数据库的部分
//
//        return user;
//    }

//    @Cacheable(value = "CrazyIMKey:User:", key = "#userid")
//    public UserPO getById(String userid) {
//        //为了简化演示，去掉数据库的部分
//
//
//       /* User u = userMapper.selectByPrimaryKey(Integer.valueOf(userid));
//        if (null == u) {
//            log.info("找不到用户信息 userid={}", userid);
//        }
//        return u;
//  */
//        return null;
//    }

//    @CacheEvict(value = "CrazyIMKey:User:", key = "#userid")
//    public int deleteById(String userid) {
//        //为了简化演示，去掉数据库的部分
//
//
///*        int u = userMapper.deleteByPrimaryKey(Integer.valueOf(userid));
//        if (0 == u) {
//            log.info("找不到用户信息 userid={}", userid);
//        }
//        return u;*/
//        return 0;
//    }

}
