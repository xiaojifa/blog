package com.hzy.blog.mapper;

import com.hzy.blog.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

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

}
