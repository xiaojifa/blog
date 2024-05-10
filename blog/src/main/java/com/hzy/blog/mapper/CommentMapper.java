package com.hzy.blog.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzy.blog.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzy.blog.vo.CommentVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzy
 * @since 2024-04-13
 */
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 文章评论列表
     * @param articleId
     * @return
     */
    IPage<CommentVo> getArticleCommentList(Page<CommentVo> commentVoPage, @Param("articleId") String articleId);
}
