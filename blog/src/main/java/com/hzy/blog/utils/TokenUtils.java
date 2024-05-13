//package com.hzy.blog.utils;
//
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.util.ObjectUtil;
//
//import cn.hutool.jwt.JWT;
//import com.hzy.blog.entity.Account;
//import com.hzy.blog.service.impl.UserServiceImpl;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.Date;
//
///**
// * Token工具类
// */
//@Component
//public class TokenUtils {
//
//    private static final Logger log = LoggerFactory.getLogger(TokenUtils.class);
//
//    private static UserServiceImpl staticUserService;
//
//    @Resource
//    private UserServiceImpl userService;
//
//    @PostConstruct
//    public void setUserService() {
//
//        staticUserService = userService;
//
//    }
//
//    /**
//     * 获取当前登录的用户信息
//     */
//    public static Account getCurrentUser() {
//        try {
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            String token = request.getHeader(Constants.TOKEN);
//            if (ObjectUtil.isNotEmpty(token)) {
//                String userRole = JWT.decode(token).getAudience().get(0);
//                String userId = userRole.split("-")[0];  // 获取用户id
//                String role = userRole.split("-")[1];    // 获取角色
//                if (RoleEnum.ADMIN.name().equals(role)) {
//                    return staticAdminService.selectById(Integer.valueOf(userId));
//                } else if (RoleEnum.USER.name().equals(role)) {
//                    return staticUserService.selectById(Integer.valueOf(userId));
//                }
//            }
//        } catch (Exception e) {
//            log.error("获取当前用户信息出错", e);
//        }
//        return new Account();  // 返回空的账号对象
//    }
//}
//
