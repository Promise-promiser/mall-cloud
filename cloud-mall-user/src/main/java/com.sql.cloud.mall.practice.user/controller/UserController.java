package com.sql.cloud.mall.practice.user.controller;

import com.sql.cloud.mall.practice.common.ApiRestResponse;
import com.sql.cloud.mall.practice.common.Constant;
import com.sql.cloud.mall.practice.exception.ImoocMallException;
import com.sql.cloud.mall.practice.exception.ImoocMallExceptionEnum;
import com.sql.cloud.mall.practice.user.model.pojo.User;
import com.sql.cloud.mall.practice.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Resource
    private UserService userService;


    /**
     * 注册用户
     * @param username
     * @param password
     * @return 用户注册成功的code,message,data
     * @throws ImoocMallException
     */
    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("username") String username,
                                    @RequestParam("password") String password) throws ImoocMallException {
        if(StringUtils.isEmpty(username)){//判断username是否为空
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isEmpty(password)){//判断password是否为空
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        if(password.length()<8){//密码长度不能小于8位
            return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(username,password);
        return ApiRestResponse.success();
    }

    /**
     * 普通用户登录
     * @param username
     * @param password
     * @param session
     * @return 用户登录状态的code,message,data
     * @throws ImoocMallException
     */
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("username") String username ,
                                 @RequestParam("password") String password, HttpSession session) throws ImoocMallException {
        if(StringUtils.isEmpty(username)){//判断username是否为空
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isEmpty(password)){//判断password是否为空
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(username,password);
        //保存用户信息时，不保存密码
        user.setPassword(null);
        //保存用户信息到session
        session.setAttribute(Constant.IMOOC_MALL_USER,user);//key=Constant.IMOOC_MALL_USER,value=user
        return ApiRestResponse.success(user);
    }

    /**
     * 更改用户个性签名
     * @return
     */
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session,
                                          @RequestParam String signature) throws ImoocMallException {
        //提取存放在session中的user对象（Constant.IMOOC_MALL_USER）
        User currentUser = (User)session.getAttribute(Constant.IMOOC_MALL_USER);
        if(currentUser == null){//如果当前用户为空，提醒我们需要登录
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);//调用更改个性签名方法
        return ApiRestResponse.success();//返回给前端
    }

    /**
     * 用户注销，清楚session
     * @param session
     * @return
     */
    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session){
        session.removeAttribute(Constant.IMOOC_MALL_USER);
        return ApiRestResponse.success();
    }

    /**
     * 管理员登录接口
     * @param username
     * @param password
     * @param session
     * @return
     * @throws ImoocMallException
     */
    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("username") String username ,
                                      @RequestParam("password") String password, HttpSession session) throws ImoocMallException {
        if(StringUtils.isEmpty(username)){//判断username是否为空
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isEmpty(password)){//判断password是否为空
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(username,password);
        //校验是否是管理员,是操作，不是报错
        if (userService.checkAdminRole(user)) {
            //是管理员，执行操作
            //保存用户信息时，不保存密码
            user.setPassword(null);
            session.setAttribute(Constant.IMOOC_MALL_USER,user);//key=Constant.IMOOC_MALL_USER,value=user
            return ApiRestResponse.success(user);
        }else{
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }

    //校验是否是管理员
    @PostMapping("/checkAdminRole")
    @ResponseBody
    public Boolean checkAdminRole(@RequestBody User user){
        return userService.checkAdminRole(user);
    }

    //获取当前登录的user对象
    @GetMapping("/getUser")//对外暴露用get
    @ResponseBody//返回json
    public User getUser(HttpSession session){
        User currentUser = (User)session.getAttribute(Constant.IMOOC_MALL_USER);
        return currentUser;
    }

}
