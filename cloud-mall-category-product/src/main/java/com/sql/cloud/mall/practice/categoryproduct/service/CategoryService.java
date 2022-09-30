package com.sql.cloud.mall.practice.categoryproduct.service;


import com.github.pagehelper.PageInfo;
import com.sql.cloud.mall.practice.categoryproduct.model.pojo.Category;
import com.sql.cloud.mall.practice.categoryproduct.model.request.AddCategoryReq;
import com.sql.cloud.mall.practice.categoryproduct.model.vo.CategoryVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryService {
    void add(AddCategoryReq addCategoryReq);

    void update(Category updateCategory);

    void delete(Integer id);

    PageInfo listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize);

    List<CategoryVO> listCategoryForCustomer();

    List<CategoryVO> listCategoryForCustomer(Integer parentId);
}
