package com.hzy.blog.service;

import com.hzy.blog.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzy.blog.vo.UserVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzy
 * @since 2024-04-13
 */
public interface IUserService extends IService<User> {

    /**
     * 根据用户id查询用户信息
     *
     * @param userId
     * @return
     */
    UserVo getUser(String userId);
}
