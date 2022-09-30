package com.sql.cloud.mall.practice.cartorder.feign;

import com.sql.cloud.mall.practice.categoryproduct.model.pojo.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "cloud-mall-category-product")
public interface ProductFeignClient {
    //Feign不经过网关
    @GetMapping("product/detailForFeign")//与category-product模块的地址一样，商品详情
    Product detailForFeign(@RequestParam Integer id);

    @PostMapping("product/updateStock")
    void updateStock(@RequestParam Integer productId, @RequestParam Integer stock);
}
