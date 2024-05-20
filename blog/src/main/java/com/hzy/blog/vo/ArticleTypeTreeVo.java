package com.hzy.blog.vo;

import com.hzy.blog.entity.Article;
import lombok.Data;

import java.util.List;

/**
 * @author 14439
 */
@Data
public class ArticleTypeTreeVo {
    private String articleTypeId;
    private String articleTypeName;
    private List<ArticleTypeTreeVo> articleTypeTreeVoList;
    private String articleTitle;
    private List<Article> articleList;
}
