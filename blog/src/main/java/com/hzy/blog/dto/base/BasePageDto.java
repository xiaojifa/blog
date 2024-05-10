package com.hzy.blog.dto.base;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/14 17:22
 */
@Data
public class BasePageDto {

    /**
     * 当前页码
     */
    @NotNull(message = "未获取到当前页码")
    private Integer pageNumber = 1;

}
