package com.hzy.blog.service.impl;

import com.hzy.blog.entity.User;
import com.hzy.blog.mapper.UserMapper;
import com.hzy.blog.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzy.blog.vo.UserVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hzy
 * @since 2024-04-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 根据用户id查询用户信息
     *
     * @param userId
     * @return
     */
    @Override
    public UserVo getUser(String userId) {
        return userMapper.getUser(userId);
    }
}
