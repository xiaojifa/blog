package com.hzy.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzy.blog.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzy.blog.vo.CommentVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzy
 * @since 2024-04-13
 */
public interface ICommentService extends IService<Comment> {

    /**
     * 文章评论列表
     * @param articleId
     * @return
     */
    IPage<CommentVo> getArticleCommentList(Page<CommentVo> commentVoPage, String articleId);
}
