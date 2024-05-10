package com.hzy.blog.dto.topic;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/21 15:09
 */
@Data
public class TopicTypeAddDto {

    /**
     * 话题分类名称
     */
    @NotBlank(message = "话题分类名称不能为空")
    private String topicTypeName;

    /**
     * 话题分类排序
     */
    @NotNull(message = "话题分类排序不能为空")
    private Integer topicTypeSort;

    /**
     * 话题分类父id
     */
    private String topicTypeParentId;


}
