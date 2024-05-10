package com.hzy.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hzy.blog.entity.TopicType;
import com.hzy.blog.vo.TopicTypeTreeVo;
import com.hzy.blog.vo.TopicTypeVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzy
 * @since 2024-05-07
 */
public interface ITopicTypeService extends IService<TopicType> {

    /**
     * 文章类型列表，包含文章数量
     * @return
     */
    List<TopicTypeVo> topicTypeList();

    /**
     * 获取首页文章类型树形目录
     * @param topicTypeParentId
     * @return
     */
    List<TopicTypeTreeVo> getIndexTopicTypeList(String topicTypeParentId);
}
