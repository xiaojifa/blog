package com.hzy.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzy.blog.entity.CommentT;
import com.hzy.blog.mapper.CommentMapper;
import com.hzy.blog.mapper.CommentTMapper;
import com.hzy.blog.service.ICommentTService;
import com.hzy.blog.vo.CommentTVo;
import com.hzy.blog.vo.CommentVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hzy
 * @since 2024-05-07
 */
@Service
public class CommentTServiceImpl extends ServiceImpl<CommentTMapper, CommentT> implements ICommentTService {
    @Resource
    private CommentTMapper commentTMapper;

    /**
     * 文章评论列表
     * @param topicId
     * @return
     */
    @Override
    public IPage<CommentTVo> getTopicCommentTList(Page<CommentTVo> commentTVoPage, String topicId) {
        return commentTMapper.getTopicCommentTList(commentTVoPage,topicId);
    }

}
