package com.sql.cloud.mall.practice.cartorder.feign;

import com.sql.cloud.mall.practice.user.model.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "cloud-mall-user")
public interface UserFeignClient {
    //获取当前登录的user对象
    @GetMapping("/getUser")
    User getUser();
}
