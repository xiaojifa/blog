package com.hzy.blog.dto.topic;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author 14439
 */
@Data
public class PublishTopicActionDto {

    /**
     * 话题id
     */
    private String topicId;
    /**
     * 话题标题
     */
    @NotBlank(message = "请填写话题标题")
    @Length(max = 480, message = "话题标题不能超过480个字符")
    private String topicTitle;
    /**
     * 话题类型id
     */
    @NotBlank(message = "请选择话题的类型")
    private String topicTypeId;
    /**
     * 话题封面url
     */
    private String topicCoverUrl;
    /**
     * 标签id列表
     */
    private String[] topicTagIds;
    /**
     * 话题内容
     */
    @NotBlank(message = "请填写话题内容")
    @Length(min = 5, max = 15000, message = "话题内容在5-15000字符之间")
    private String topicContext;


}
