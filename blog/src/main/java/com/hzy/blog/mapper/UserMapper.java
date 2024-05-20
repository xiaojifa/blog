package com.hzy.blog.mapper;

import com.hzy.blog.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzy.blog.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzy
 * @since 2024-04-13
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户id获取用户信息
     *
     * @param userId
     * @return
     */
    UserVo getUser(@Param("userId") String userId);


}
