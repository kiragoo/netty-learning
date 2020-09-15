package com.kirago.netty.im.gateway.controller;

import com.kirago.netty.im.common.entity.DTO.UserDTO;
import com.kirago.netty.im.common.entity.ImNode;
import com.kirago.netty.im.common.entity.LoginBack;
import com.kirago.netty.im.common.util.JsonUtil;
import com.kirago.netty.im.gateway.balance.ImLoadBalance;
import com.kirago.netty.im.gateway.mybatis.entity.UserPO;
import com.kirago.netty.im.gateway.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/user")
@Api("User 相关的api")
public class UserController extends BaseController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ImLoadBalance imLoadBalance;

    /**
     * Web短连接登录
     *
     * @param username 用户名
     * @param password 命名
     * @return 登录结果
     */
    @ApiOperation(value = "登录", notes = "根据用户信息登录")
    @RequestMapping(value = "/login/{username}/{password}",method = RequestMethod.GET)
    public String loginAction(
            @PathVariable("username") String username,
            @PathVariable("password") String password) {
        UserPO user = new UserPO();
        user.setUserName(username);
        user.setPassWord(password);
        user.setUserId(user.getUserName());

//        User loginUser = userService.login(user);

        LoginBack back = new LoginBack();
        /**
         * 取得最佳的Netty服务器
         */
        ImNode bestWorker = imLoadBalance.getBestWorker();
        back.setImNode(bestWorker);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
//        back.setUserDTO(userDTO);
        back.setToken(user.getUserId().toString());
        return JsonUtil.object2JsonString(back);
    }

    /**
     * 从zookeeper中删除所有IM节点
     *
     * @return 删除结果
     */
    @ApiOperation(value = "删除节点", notes = "从zookeeper中删除所有IM节点")
    @RequestMapping(value = "/removeWorkers",method = RequestMethod.GET)
    public String removeWorkers(){
        imLoadBalance.removeWorkers();
        return "已经删除";
    }

}