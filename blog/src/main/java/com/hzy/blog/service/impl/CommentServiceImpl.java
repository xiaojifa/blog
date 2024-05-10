package com.hzy.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzy.blog.entity.Comment;
import com.hzy.blog.mapper.CommentMapper;
import com.hzy.blog.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzy.blog.vo.CommentVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hzy
 * @since 2024-04-13
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    @Resource
    private CommentMapper commentMapper;

    /**
     * 文章评论列表
     * @param articleId
     * @return
     */
    @Override
    public IPage<CommentVo> getArticleCommentList(Page<CommentVo> commentVoPage, String articleId) {
        return commentMapper.getArticleCommentList(commentVoPage,articleId);
    }
}
