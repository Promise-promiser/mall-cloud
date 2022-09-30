package com.sql.cloud.mall.practice.cartorder.config;


import com.sql.cloud.mall.practice.categoryproduct.common.ProductConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImoocMallWebMvcConfigurer implements WebMvcConfigurer {

    @Value("${file.upload.dir}")
    String FILE_UPLOAD_DIR;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("/admin/**").addResourceLocations("classpath:/static/admin/");
        //自定义静态资源映射目录（把url地址映射到本地目录）；凡是以images开头的都会被转发到FILE_UPLOAD_DIR地址所对应的file
        registry.addResourceHandler("/images/**").addResourceLocations("file:" + FILE_UPLOAD_DIR);
    }
}
