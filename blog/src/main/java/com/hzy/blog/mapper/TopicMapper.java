package com.hzy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzy.blog.entity.Topic;
import com.hzy.blog.vo.TopicVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzy
 * @since 2024-05-07
 */
public interface TopicMapper extends BaseMapper<Topic> {
    /**
     * 文章列表
     *
     * @param topicPage
     * @param topicTitle
     * @return
     */
    IPage<TopicVo> topicList(IPage<TopicVo> topicPage, @Param("topicTitle") String topicTitle, @Param("userId") String userId);

    /**
     * 文章列表 前端
     *
     * @param topicPage
     * @param topicTitle
     * @param topicTypeId
     * @return
     */
    IPage<TopicVo> topicListView(Page<TopicVo> topicPage, String topicTitle, String topicTypeId);

    /**
     * 首页最新文章
     * @return
     */
    List<TopicVo> getIndexTopicList();

    /**
     * 根据文章id获取文章信息
     * @param topicId
     * @return
     */
    TopicVo getTopic(@Param("topicId") String topicId);

    /**
     * 获取标签对应的文章列表
     * @param topicPage
     * @param topicTagId
     * @return
     */
    IPage<TopicVo> tagTopicList(Page<TopicVo> topicPage,@Param("topicTagId") String topicTagId);
}
