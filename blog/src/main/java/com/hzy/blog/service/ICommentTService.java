package com.hzy.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzy.blog.entity.CommentT;
import com.hzy.blog.vo.CommentTVo;
import com.hzy.blog.vo.CommentVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzy
 * @since 2024-05-07
 */
public interface ICommentTService extends IService<CommentT> {

    /**
     * 文章评论列表
     * @param topicId
     * @return
     */
    IPage<CommentTVo> getTopicCommentTList(Page<CommentTVo> commentTVoPage, String topicId);

}
