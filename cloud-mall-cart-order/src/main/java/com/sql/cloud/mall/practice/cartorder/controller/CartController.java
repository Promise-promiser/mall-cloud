package com.sql.cloud.mall.practice.cartorder.controller;


import com.sql.cloud.mall.practice.cartorder.feign.UserFeignClient;
import com.sql.cloud.mall.practice.cartorder.model.vo.CartVO;
import com.sql.cloud.mall.practice.cartorder.service.CartService;
import com.sql.cloud.mall.practice.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController//返回json格式
@RequestMapping("cart")
public class CartController {

    @Autowired
    CartService cartService;
    @Autowired
    UserFeignClient userFeignClient;

    @GetMapping("/list")
    @ApiOperation("购物车列表")
    public ApiRestResponse list(){//@RequestParam方便与url进行绑定
        //userFeignClient.getUser().getId()不用进行空判断，因为filter拦截cart
        List<CartVO> cartList = cartService.list(userFeignClient.getUser().getId());
        return ApiRestResponse.success(cartList);
    }


    @PostMapping("/add")
    @ApiOperation("添加商品到购物车")
    public ApiRestResponse add(@RequestParam Integer productId,
                               @RequestParam Integer count){//@RequestParam方便与url进行绑定
        List<CartVO> cartVOList = cartService.add(userFeignClient.getUser().getId(),productId,count);
        return ApiRestResponse.success();
    }

    @PostMapping("/update")
    @ApiOperation("更改购物车")
    public ApiRestResponse update(@RequestParam Integer productId,
                                  @RequestParam Integer count){//@RequestParam方便与url进行绑定
        List<CartVO> cartVOList = cartService.update(userFeignClient.getUser().getId(),productId,count);
        return ApiRestResponse.success();
    }

    @PostMapping("/delete")
    @ApiOperation("删除购物车")
    public ApiRestResponse delete(@RequestParam Integer productId){//@RequestParam方便与url进行绑定
        //不能 **传入** userId,cartId,否则可以删除别人的购物车
        List<CartVO> cartVOList = cartService.delete(userFeignClient.getUser().getId(),productId);
        return ApiRestResponse.success();
    }

    @PostMapping("/select")
    @ApiOperation("选择/不选择购物车的某商品")
    public ApiRestResponse select(@RequestParam Integer productId,
                                  @RequestParam Integer selected){//@RequestParam方便与url进行绑定
        List<CartVO> cartVOList = cartService.selectOrNot(userFeignClient.getUser().getId(),productId,selected);
        return ApiRestResponse.success(cartVOList);
    }

    @PostMapping("/selectAll")
    @ApiOperation("全择/全不选购物车的某商品")
    public ApiRestResponse selectAll(@RequestParam Integer selected){//@RequestParam方便与url进行绑定
        List<CartVO> cartVOList = cartService.selectAllOrNot(userFeignClient.getUser().getId(),selected);
        return ApiRestResponse.success(cartVOList);
    }
}
