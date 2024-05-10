package com.hzy.blog.dto.article;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/21 19:58
 */
@Data
public class ArticleTypeUpdateDto {

    /**
     * 文章分类id
     */
    @NotBlank(message = "文章分类id 不能为空")
    private String articleTypeId;

    /**
     * 文章分类名称
     */
    private String articleTypeName;

    /**
     * 文章分类排序
     */
    private Integer articleTypeSort;

    /**
     * 文章分类父id
     */
    private String articleTypeParentId;


}
