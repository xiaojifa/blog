package com.hzy.blog.dto.article;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/21 15:09
 */
@Data
public class ArticleTypeAddDto {

    /**
     * 文章分类名称
     */
    @NotBlank(message = "文章分类名称不能为空")
    private String articleTypeName;

    /**
     * 文章分类排序
     */
    @NotNull(message = "文章分类排序不能为空")
    private Integer articleTypeSort;

    /**
     * 文章分类父id
     */
    private String articleTypeParentId;


}
