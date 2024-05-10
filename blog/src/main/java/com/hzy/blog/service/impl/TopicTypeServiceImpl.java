package com.hzy.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzy.blog.entity.TopicType;
import com.hzy.blog.mapper.TopicTypeMapper;
import com.hzy.blog.service.ITopicTypeService;
import com.hzy.blog.vo.ArticleTypeTreeVo;
import com.hzy.blog.vo.ArticleTypeVo;
import com.hzy.blog.vo.TopicTypeTreeVo;
import com.hzy.blog.vo.TopicTypeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hzy
 * @since 2024-05-07
 */
@Service
public class TopicTypeServiceImpl extends ServiceImpl<TopicTypeMapper, TopicType> implements ITopicTypeService {

    @Resource
    private TopicTypeMapper topicTypeMapper;

    /**
     * 文章类型列表，包含文章数量
     * @return
     */
    @Override
    public List<TopicTypeVo> topicTypeList() {
        return topicTypeMapper.topicTypeList();
    }

    /**
     * 获取首页文章类型树形目录
     * @param topicTypeParentId
     * @return
     */
    @Override
    public List<TopicTypeTreeVo> getIndexTopicTypeList(String topicTypeParentId) {
        return topicTypeMapper.getIndexTopicTypeList(topicTypeParentId);
    }
}
