package com.sql.cloud.mall.practice.categoryproduct.controller;


import com.github.pagehelper.PageInfo;
import com.sql.cloud.mall.practice.categoryproduct.model.pojo.Category;
import com.sql.cloud.mall.practice.categoryproduct.model.request.AddCategoryReq;
import com.sql.cloud.mall.practice.categoryproduct.model.request.UpdateCategoryReq;
import com.sql.cloud.mall.practice.categoryproduct.model.vo.CategoryVO;
import com.sql.cloud.mall.practice.categoryproduct.service.CategoryService;
import com.sql.cloud.mall.practice.common.ApiRestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 后台添加商品分类；需要检验 1.是否登录 2.是否为管理员
     * @param session
     * @param addCategoryReq
     * @return
     */
    @ApiOperation("后台添加商品分类")
    @PostMapping("admin/category/add")
    @ResponseBody
    public ApiRestResponse addCategory(HttpSession session,
                                       @Valid @RequestBody AddCategoryReq addCategoryReq){
            categoryService.add(addCategoryReq);
            return ApiRestResponse.success();
    }

    @ApiOperation("后台更改商品分类")
    @PostMapping("admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(@Valid @RequestBody UpdateCategoryReq updateCategoryReq,
                                          HttpSession session){
            Category category = new Category();
            BeanUtils.copyProperties(updateCategoryReq,category);
            categoryService.update(category);
            return ApiRestResponse.success();
    }

    @ApiOperation("后台删除商品分类")
    @PostMapping("admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleteCategory(@RequestParam Integer id ){
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台目录列表")
    @PostMapping("admin/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = categoryService.listCategoryForAdmin(pageNum,pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台目录列表")
    @PostMapping("category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer(){
        List<CategoryVO> categoryVOS = categoryService.listCategoryForCustomer();
        return ApiRestResponse.success(categoryVOS);
    }
}
