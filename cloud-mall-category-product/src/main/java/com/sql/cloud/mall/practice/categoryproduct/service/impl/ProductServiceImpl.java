package com.sql.cloud.mall.practice.categoryproduct.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sql.cloud.mall.practice.categoryproduct.model.dao.ProductMapper;
import com.sql.cloud.mall.practice.categoryproduct.model.pojo.Product;
import com.sql.cloud.mall.practice.categoryproduct.model.query.ProductListQuery;
import com.sql.cloud.mall.practice.categoryproduct.model.request.AddProductReq;
import com.sql.cloud.mall.practice.categoryproduct.model.request.ProductListReq;
import com.sql.cloud.mall.practice.categoryproduct.model.vo.CategoryVO;
import com.sql.cloud.mall.practice.categoryproduct.service.CategoryService;
import com.sql.cloud.mall.practice.categoryproduct.service.ProductService;
import com.sql.cloud.mall.practice.common.Constant;
import com.sql.cloud.mall.practice.exception.ImoocMallException;
import com.sql.cloud.mall.practice.exception.ImoocMallExceptionEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    CategoryService categoryService;

    /**
     * 新增商品service
     * @param addProductReq
     */
    @Override
    public void add(AddProductReq addProductReq){
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq,product);
        Product productOld = productMapper.selectByName(product.getName());
        if(productOld != null){
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        product.setCreateTime(new Date());
        int count = productMapper.insertSelective(product);
        if(count == 0){
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }

    /**
     * 更新商品service
     * @param updateProduct
     */
    @Override
    public void update(Product updateProduct){
        Product productOld = productMapper.selectByName(updateProduct.getName());
        //查找对应name不为空，且id与新传入的id相同，则不能进行新增操作（新增同名不同id，失败）
        if(productOld != null && !productOld.getId().equals(updateProduct.getId())){
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(updateProduct);
        if(count == 0){
            throw new ImoocMallException(ImoocMallExceptionEnum.UPLOAD_FAILED);
        }
    }

    /**
     * 删除商品service
     * @param id
     */
    @Override
    public void delete(Integer id){
        Product productOld = productMapper.selectByPrimaryKey(id);
        //查不到记录，无法删除
        if(productOld == null){
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if(count == 0){
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    /**
     * 批量上下架service
     * @param ids
     * @param sellStatus
     */
    @Override
    public void batchUpdateSellStatus(Integer[] ids,Integer sellStatus){
        productMapper.batchUpdateSellStatus(ids,sellStatus);
    }


    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.selectListForAdmin();
        //将结果放入PageINfo中
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }

    @Override
    public Product detail(Integer id){
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }

    @Override
    public PageInfo list(ProductListReq productListReq){
        //构建Query对象
        ProductListQuery productListQuery = new ProductListQuery();
        //搜索处理
        if(!StringUtils.isEmpty(productListReq.getKeyword())){
            String keyword = new StringBuilder().append("%").append(productListReq.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }
        //目录处理：如果查询某个目录下的商品，不仅要查询出该目录下的，还要查询出所有子目录下的商品，所以要拿到一个目录的id的List
        if(productListReq.getCategoryId() != null){
            //查询子目录
            List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(productListReq.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            //嵌套，用下面的方法
            getCategoryIds(categoryVOList,categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }
        //排序处理
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
            PageHelper.startPage(productListReq.getPageNum(),productListReq.getPageSize(),orderBy);
        }else {
            PageHelper.startPage(productListReq.getPageNum(),productListReq.getPageSize());
        }
        List<Product> productList = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }
    private void getCategoryIds(List<CategoryVO> categoryVOList, ArrayList<Integer> categoryIds){
        //遍历CategoryVOList
        for(int i = 0 ; i<categoryVOList.size() ; i++){
            CategoryVO categoryVO = categoryVOList.get(i);
            if(categoryVO != null){
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(),categoryIds);
            }
        }
    }

    @Override
    public void updateStock(Integer productId, Integer stock){
        Product product = new Product();
        product.setId(productId);
        product.setStock(stock);
        //updateByPrimaryKey表示所有的都要传入
        productMapper.updateByPrimaryKeySelective(product);
    }
}
