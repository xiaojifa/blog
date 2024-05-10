package com.hzy.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzy.blog.dto.topic.PublishTopicActionDto;
import com.hzy.blog.entity.Topic;
import com.hzy.blog.entity.User;
import com.hzy.blog.utils.CommonResult;
import com.hzy.blog.vo.TopicVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzy
 * @since 2024-05-07
 */
public interface ITopicService extends IService<Topic> {

    /**
     * 文章列表
     * @param topicPage
     * @param topicTitle
     * @return
     */
    IPage<TopicVo> topicList(IPage<TopicVo> topicPage, String topicTitle, String userId);

    /**
     * 文章列表 前端
     * @param topicPage
     * @param topicTitle
     * @param topicTypeId
     * @return
     */
    IPage<TopicVo> topicListView(Page<TopicVo> topicPage, String topicTitle, String topicTypeId);

    /**
     * 发布文章方法
     * @param publishTopicActionDto
     * @return
     */
    CommonResult publishTopicAction(HttpServletRequest request, PublishTopicActionDto publishTopicActionDto);

    /**
     * 删除文章
     * @param topicId
     * @return
     */
    CommonResult delTopic(String topicId);

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
    TopicVo getTopic(String topicId);

    /**
     * 收藏文章
     * @param user
     * @param topicId
     * @return
     */
    CommonResult topicCollection(User user, String topicId);

    /**
     * 获取标签对应的文章列表
     * @param topicPage
     * @param topicTagId
     * @return
     */
    IPage<TopicVo> tagTopicList(Page<TopicVo> topicPage, String topicTagId);
}
