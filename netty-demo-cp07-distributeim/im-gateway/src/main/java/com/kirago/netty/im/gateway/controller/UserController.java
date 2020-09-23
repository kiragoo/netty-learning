package com.kirago.netty.im.gateway.controller;

import com.kirago.netty.im.common.entity.DTO.UserDTO;
import com.kirago.netty.im.common.entity.PT.UserPT;
import com.kirago.netty.im.common.util.api.Response;
import com.kirago.netty.im.common.util.api.ResponseResult;
import com.kirago.netty.im.gateway.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(value = "/user")
@Api("User 相关的api")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @ApiOperation(value = "注册", notes = "用户注册")
    @PostMapping(value = "/register")
    public ResponseResult register(@RequestBody UserDTO userDTO){
         userService.register(userDTO);
         return Response.makeRsp(200,"注册用户成功", userDTO);
    }
    
    @ApiOperation(value = "登录", notes = "用户登录")
    @PostMapping(value = "/login")
    public ResponseResult login(@RequestBody UserDTO userDTO){
        UserPT userPT =  userService.login(userDTO);
        if(userPT != null) {
            return Response.makeRsp(200, "用户登录成功", userPT);
        }else {
            return Response.makeRsp(200, "未注册用户", null);
        }
    }

    /**
     * Web短连接登录
     *
     * @param username 用户名
     * @param password 命名
     * @return 登录结果
     */
//    @ApiOperation(value = "登录", notes = "根据用户信息登录")
//    @RequestMapping(value = "/login/{username}/{password}",method = RequestMethod.GET)
//    public String loginAction(
//            @PathVariable("username") String username,
//            @PathVariable("password") String password) {
//        UserPO user = new UserPO();
//        user.setUserName(username);
//        user.setPassWord(password);
//        user.setUserId(user.getUserName());
//
////        User loginUser = userService.login(user);
//
//        LoginBack back = new LoginBack();
//        /**
//         * 取得最佳的Netty服务器
//         */
//        ImNode bestWorker = imLoadBalance.getBestWorker();
//        back.setImNode(bestWorker);
//        UserDTO userDTO = new UserDTO();
//        BeanUtils.copyProperties(user, userDTO);
////        back.setUserDTO(userDTO);
//        back.setToken(user.getUserId().toString());
//        return JsonUtil.object2JsonString(back);
//    }

//    /**
//     * 从zookeeper中删除所有IM节点
//     *
//     * @return 删除结果
//     */
//    @ApiOperation(value = "删除节点", notes = "从zookeeper中删除所有IM节点")
//    @RequestMapping(value = "/removeWorkers",method = RequestMethod.GET)
//    public String removeWorkers(){
//        imLoadBalance.removeWorkers();
//        return "已经删除";
//    }

}