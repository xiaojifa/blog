package com.hzy.blog.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
public class UserCollectionTopic implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_collection_topic_id")
    private String userCollectionTopicId;

    private String userId;

    private String topicId;

    private Date userCollectionTopicTime;


}
