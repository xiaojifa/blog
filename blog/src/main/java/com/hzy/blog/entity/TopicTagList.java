package com.hzy.blog.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzy
 * @since 2024-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TopicTagList implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "topic_tag_list_id")
    private String topicTagListId;

    private String topicId;

    private String topicTagId;


}
