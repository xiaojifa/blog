package com.hzy.blog.vo;

import lombok.Data;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/10 17:44
 */
@Data
public class CommentTVo {
    /**
     * 文章评论id
     */
    private String commentTId;

    /**
     * 话题id
     */
    private String topicId;

    /**
     * 用户id（评论人）
     */
    private String userId;

    /**
     * 话题评论内容
     */
    private String commentTContent;

    /**
     * 评论时间
     */
    private String commentTTime;

    /**
     * 点赞次数
     */
    private Integer commentTGoodNumber;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 是否点过赞，0未点赞，1已经点赞
     */
    private Integer isGoodCommentT;
}
