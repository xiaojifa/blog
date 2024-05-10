package com.hzy.blog.dto.article;

import com.hzy.blog.dto.base.BasePageDto;
import lombok.Data;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/25 16:24
 */
@Data
public class ArticlePageDto extends BasePageDto {

    /**
     * 文章标题
     */
    private String articleTitle;
}