package com.hzy.blog.vo;

import com.hzy.blog.entity.Topic;
import lombok.Data;

import java.util.List;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/7 19:40
 */
@Data
public class TopicTypeTreeVo {
    private String topicTypeId;
    private String topicTypeName;
    private List<TopicTypeTreeVo> topicTypeTreeVoList;
    private List<Topic> topicList;
}
