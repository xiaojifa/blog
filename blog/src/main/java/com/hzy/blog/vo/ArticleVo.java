package com.hzy.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/25 16:46
 */
@Data
public class ArticleVo {

    /**
     * 文章id
     */
    private String articleId;

    /**
     * 文章分类id
     */
    private String articleTypeId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章封面url
     */
    private String articleCoverUrl;

    /**
     * 文章添加时间
     */
    private LocalDateTime articleAddTime;

    /**
     * 是否是热门文章 0否，1是
     */
    private Integer articleHot;

    /**
     * 点赞次数
     */
    private Integer articleGoodNumber;

    /**
     * 观看次数
     */
    private Integer articleLookNumber;

    /**
     * 收藏次数
     */
    private Integer articleCollectionNumber;

    /**
     * 文章类型名称
     */
    private String articleTypeName;

    /**
     * 文章内容
     */
    private String articleContext;


}
