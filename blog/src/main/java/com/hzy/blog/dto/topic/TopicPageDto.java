package com.hzy.blog.dto.topic;

import com.hzy.blog.dto.base.BasePageDto;
import lombok.Data;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/25 16:24
 */
@Data
public class TopicPageDto extends BasePageDto {

    /**
     * 话题标题
     */
    private String topicTitle;


}