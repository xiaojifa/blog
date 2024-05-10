package com.hzy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzy.blog.entity.TopicType;
import com.hzy.blog.vo.TopicTypeTreeVo;
import com.hzy.blog.vo.TopicTypeVo;
import com.hzy.blog.vo.TopicTypeTreeVo;
import com.hzy.blog.vo.TopicTypeVo;
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
public interface TopicTypeMapper extends BaseMapper<TopicType> {

    /**
     * 文章类型列表，包含文章数量
     * @return
     */
    List<TopicTypeVo> topicTypeList();

    /**
     * 获取首页文章类型树形目录
     * @return
     */
    List<TopicTypeTreeVo> getIndexTopicTypeList(@Param("topicTypeParentId") String topicTypeParentId);
}
