package com.kirago.netty.im.gateway.mapper;

import com.kirago.netty.im.gateway.entity.DO.UserDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserDO record);

    int insertSelective(UserDO record);

    UserDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);
    
    UserDO selectByUsername(String username);
}