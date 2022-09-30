package com.sql.cloud.mall.practice.categoryproduct.controller;

import com.github.pagehelper.PageInfo;
import com.sql.cloud.mall.practice.categoryproduct.common.ProductConstant;
import com.sql.cloud.mall.practice.categoryproduct.model.pojo.Product;
import com.sql.cloud.mall.practice.categoryproduct.model.request.AddProductReq;
import com.sql.cloud.mall.practice.categoryproduct.model.request.UpdateProductReq;
import com.sql.cloud.mall.practice.categoryproduct.service.ProductService;
import com.sql.cloud.mall.practice.common.ApiRestResponse;
import com.sql.cloud.mall.practice.exception.ImoocMallException;
import com.sql.cloud.mall.practice.exception.ImoocMallExceptionEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * 后台商品Controller
 */

@RestController //代表里面每个都加了@ResponseBody
public class ProductAdminController {


    @Autowired
    private ProductService productService;

    @Value("${file.upload.ip}")
    String ip;

    @Value("${file.upload.port}")
    Integer port;


    /**
     * 商品新增接口
     * @param addProductReq
     * @return
     */
    @PostMapping("/admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq){
       productService.add(addProductReq);
       return ApiRestResponse.success();
    }

    /**
     * 图片上传接口
     * @param httpServletRequest
     * @param file
     * @return
     */
    @PostMapping("/admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file ) {
        //获取后缀
        String fileName = file.getOriginalFilename();//得到原始名字
        String suffixName = fileName.substring(fileName.lastIndexOf("."));//获取后缀；结果是（.jpg）等
        //生成文件名称UUID
        UUID uuid = UUID.randomUUID();
        //文件名称
        String newFileName = uuid.toString() + suffixName;
        //生成文件夹
        File fileDirectory = new File(ProductConstant.FILE_UPLOAD_DIR);//Constant.FILE_LOAD_DIR是文件上传的地址
        //生成文件  destFile：目标文件
        File destFile = new File(ProductConstant.FILE_UPLOAD_DIR + newFileName);//文件夹+文件名
        if (!fileDirectory.exists()) {//文件夹是否存在；不存在新建
            if (!fileDirectory.mkdir()) {//文件夹不存在，创建失败
                throw new ImoocMallException(ImoocMallExceptionEnum.MKDIR_FAILED);
            }
        }
            try {
                file.transferTo(destFile);//file是传入的参数，用transferTo将其传入（写到）到destFile里面
            } catch (IOException e) {
                e.printStackTrace();
            }
            //把文件从请求中放入文件目录下的地址中

        try {
            //new URI（）;httpServletRequest.getRequestURL()获取到当前的uri ; +"" 是为了转化为字符串类型；
            return ApiRestResponse.success(getHost(new URI (httpServletRequest.getRequestURL()+""))+"/category-product/images/"+newFileName );//路径
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.UPLOAD_FAILED);
        }
    }

    //用于获取IP和端口号；通过这个方法能获取目标uri，即我们需要的uri，将无用的信息剔除
    private URI getHost(URI uri){
        URI effectiveURI;//定义一个uri
        try {
            effectiveURI = new URI(uri.getScheme(),uri.getUserInfo(),ip , port,null,null,null);
        } catch (URISyntaxException e) {
            effectiveURI = null;//新建失败设置为null
        }
        return effectiveURI;
    }

    @ApiOperation("后台更新商品")
    @PostMapping("/admin/product/update")
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq){
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq,product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除商品")
    @PostMapping("/admin/product/delete")
    public ApiRestResponse deleteProduct(@RequestParam Integer id){
        productService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台批量上下架接口")
    @PostMapping("admin/product/batchUpdateSellStatus")
    public ApiRestResponse batchUpdateSellStatus(Integer ids[], Integer sellStatus){
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台商品列表")
    @PostMapping("admin/product/list")
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize){

        PageInfo pageInfo = productService.listForAdmin(pageNum,pageSize);
        return ApiRestResponse.success(pageInfo);
    }
}
