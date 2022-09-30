package com.sql.cloud.mall.practice.categoryproduct.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sql.cloud.mall.practice.categoryproduct.model.dao.CategoryMapper;
import com.sql.cloud.mall.practice.categoryproduct.model.pojo.Category;
import com.sql.cloud.mall.practice.categoryproduct.model.request.AddCategoryReq;
import com.sql.cloud.mall.practice.categoryproduct.model.vo.CategoryVO;
import com.sql.cloud.mall.practice.categoryproduct.service.CategoryService;
import com.sql.cloud.mall.practice.exception.ImoocMallException;
import com.sql.cloud.mall.practice.exception.ImoocMallExceptionEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 目录分类实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 新增目录
     * @param addCategoryReq
     */
    @Override
    public void add(AddCategoryReq addCategoryReq){
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReq,category);//将addCategoryReq的属性放到category中
        Category categoryOld = categoryMapper.selectByName(category.getName());
        if(categoryOld != null){//不允许重名
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        category.setCreateTime(new Date());
        int count = categoryMapper.insertSelective(category);
        if (count == 0){//更新数据为0
            throw new ImoocMallException(ImoocMallExceptionEnum.CREATE_FAILED);
        }
    }

    /**
     * 更新目录
     * @param updateCategory
     */
    @Override
    public void update(Category updateCategory){
        if(updateCategory.getName() != null){
            //有这个name，而且这个name的id和updateCategory的id不同，说明name已存在
            Category categoryOld = categoryMapper.selectByName(updateCategory.getName());
            if(categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())){
                throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
            }
            //对数据进行更新的操作
            updateCategory.setUpdateTime(new Date());
            int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
            if(count == 0){
                throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
            }
        }
    }

    /**
     * 删除目录
     * @param id
     */
    @Override
    public void delete(Integer id){
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        //查不到记录，无法删除，删除失败
        if(categoryOld == null){
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if(count == 0){
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    /**
     * 目录列表（给管理员看）
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo listCategoryForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageHelper.startPage(pageNum,pageSize,"type,order_num");//引号里面的是排序
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo(categoryList);//PageInfo是返回给前端的一个类型
        return pageInfo;
    }

    /**
     * 目录列表（给用户看）
     * @return
     */
    @Override
    @Cacheable(value = "listCategoryForCustomer")
    public List<CategoryVO> listCategoryForCustomer(){
        ArrayList<CategoryVO> categoryVOList = new ArrayList<>();
        recursivelyFindCategories(categoryVOList,0);//recursively递归
        return categoryVOList;
    }


    private void recursivelyFindCategories(List<CategoryVO> categoryVOList, Integer parentId){
        //递归获取所有子类别，并组合成一个”目录树“
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if(!CollectionUtils.isEmpty(categoryList)){//不为空
            for(int i = 0; i < categoryList.size(); i++){//逐个遍历
                Category category = categoryList.get(i);
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category,categoryVO);//将categoryVO中除了childCategory字段赋值
                categoryVOList.add(categoryVO);
                //将childCategory赋值
                recursivelyFindCategories(categoryVO.getChildCategory(),categoryVO.getId());
            }
        }
    }
    public List<CategoryVO> listCategoryForCustomer(Integer parentId){
        ArrayList<CategoryVO> categoryVOList = new ArrayList<>();
        recursivelyFindCategories(categoryVOList,parentId);//recursively递归
        return categoryVOList;
    }
}
