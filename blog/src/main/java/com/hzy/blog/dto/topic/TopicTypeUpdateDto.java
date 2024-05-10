package com.hzy.blog.dto.topic;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/21 19:58
 */
@Data
public class TopicTypeUpdateDto {

    /**
     * 话题分类id
     */
    @NotBlank(message = "话题分类id 不能为空")
    private String topicTypeId;

    /**
     * 话题分类名称
     */
    private String topicTypeName;

    /**
     * 话题分类排序
     */
    private Integer topicTypeSort;

    /**
     * 话题分类父id
     */
    private String topicTypeParentId;


}
