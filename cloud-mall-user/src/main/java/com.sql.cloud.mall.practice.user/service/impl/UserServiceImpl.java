package com.sql.cloud.mall.practice.user.service.impl;


import com.sql.cloud.mall.practice.exception.ImoocMallException;
import com.sql.cloud.mall.practice.exception.ImoocMallExceptionEnum;
import com.sql.cloud.mall.practice.user.model.dao.UserMapper;
import com.sql.cloud.mall.practice.user.model.pojo.User;
import com.sql.cloud.mall.practice.user.service.UserService;
import com.sql.cloud.mall.practice.utils.MD5Utils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;



    /**
     * 注册用户
     * @param username
     * @param password
     * @throws ImoocMallException
     */
    @Override
    public void register(String username, String password) throws ImoocMallException {
        //检验是否重名
        User result = userMapper.selectByName(username);//通过用户名查找用户
        if(result != null){//有重复的用户名
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        //写到数据库
        User user = new User();
        user.setUsername(username);
        user.setRole(1);
        user.setCreateTime(new Date());
        try {
            user.setPassword(MD5Utils.getMD5Str(password));//向数据库存加盐以后的密码
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //插入数据库
        int count = userMapper.insertSelective(user);//返回的是往数据库里面插入的数据数量
        if(count == 0){
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public User login(String username, String password) throws ImoocMallException {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //检验用户名密码与数据库是否匹配
        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    /**
     * 更改用户个性签名
     * @param user
     * @throws ImoocMallException
     */
    @Override
    public void updateInformation(User user ) throws ImoocMallException {
        //更改个性签名
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 1 ){//如果更新的数据大于1，则数据库更新失败
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    /**
     * 判断用户是普通用户还是管理员用户
     * @param user
     * @return
     */
    @Override
    public boolean checkAdminRole(User user){
        //1是普通用户，2是管理员用户
        return user.getRole().equals(2);
    }
}
