package com.siro.mall.config;

import com.siro.mall.common.Constants;
import com.siro.mall.interceptor.AdminLoginInterceptor;
import com.siro.mall.interceptor.MallCartNumberInterceptor;
import com.siro.mall.interceptor.MallLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author starsea
 * @date 2021-01-22
 */
@Configuration
public class MallWebMvcConfigurer implements WebMvcConfigurer {

    @Resource
    private AdminLoginInterceptor adminLoginInterceptor;
    @Resource
    private MallLoginInterceptor mallLoginInterceptor;
    @Resource
    private MallCartNumberInterceptor mallCartNumberInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        // 添加一个拦截器，拦截以/admin为前缀的url路径（后台登陆拦截）swagger-ui.html
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login")
                .excludePathPatterns("/admin/dist/**")
                .excludePathPatterns("/admin/plugins/**")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/swagger-ui.html/**");
        // 购物车中的数量统一处理
        registry.addInterceptor(mallCartNumberInterceptor)
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/logout")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/swagger-ui.html/**");
        // 商城页面登录拦截
        registry.addInterceptor(mallLoginInterceptor)
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/logout")
                .addPathPatterns("/goods/detail/**")
                .addPathPatterns("/shop-cart")
                .addPathPatterns("/shop-cart/**")
                .addPathPatterns("/saveOrder")
                .addPathPatterns("/orders")
                .addPathPatterns("/orders/**")
                .addPathPatterns("/personal")
                .addPathPatterns("/personal/updateInfo")
                .addPathPatterns("/selectPayType")
                .addPathPatterns("/payPage")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/swagger-ui.html/**");
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
        registry.addResourceHandler("/goods-img/**")
                .addResourceLocations("file:" + Constants.FILE_UPLOAD_DIC);
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
