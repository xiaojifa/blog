package com.hzy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzy.blog.entity.CommentT;
import com.hzy.blog.vo.CommentTVo;
import com.hzy.blog.vo.CommentVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzy
 * @since 2024-05-07
 */
public interface CommentTMapper extends BaseMapper<CommentT> {

    /**
     * 文章评论列表
     * @param topicId
     * @return
     */
    IPage<CommentTVo> getTopicCommentTList(Page<CommentTVo> commentTVoPage, @Param("topicId") String topicId);

}
