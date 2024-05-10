package com.hzy.blog.config;

import com.hzy.blog.intercepter.AdminInterceptor;
import com.hzy.blog.intercepter.UserInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Administrator
 * @program: xfg
 * @description: 全局配置
 * @create: 2019-12-14 11:21
 */
@Configuration
public class GlobalConfig implements WebMvcConfigurer {
//    /**
//     * 全局拦截器
//     * @return
//     */
//    @Bean
//    public HandlerInterceptor getGlobalIntercepter() {
//        return new GlobalIntercepter();
//    }

    /**
     * 让GlobalIntercepter提前加载，否则 在GlobalIntercepter 里面使用 @Autowired 会注入失败
     *
     * @return
     */
    @Bean
    public HandlerInterceptor getAdminInterceptor() {
        return new AdminInterceptor();
    }

    /**
     * 用户拦截器
     * @return
     */
    @Bean
    public HandlerInterceptor getUserInterceptor() {
        return new UserInterceptor();
    }


    /**
     * 添加全局拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //管理员拦截
        registry.addInterceptor(getAdminInterceptor()).addPathPatterns("/hzy2003/**")
                .excludePathPatterns("/hzy2003/login", "/hzy2003/logout", "/hzy2003/adminLogin");

        //用户拦截器
        registry.addInterceptor(getUserInterceptor()).addPathPatterns("/user/**");

//        //用户拦截器
//        registry.addInterceptor(getGlobalIntercepter())
//                .addPathPatterns("/**")
//                .excludePathPatterns("/static/**","/css/**","/js/**","/img/**");
    }
}
