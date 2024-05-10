package com.hzy.blog.dto.user;

import com.hzy.blog.dto.base.BasePageDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/14 17:24
 */
@Data
public class UserListPageDto extends BasePageDto {

    /**
     * 用户名
     */
    private String userName;
}
