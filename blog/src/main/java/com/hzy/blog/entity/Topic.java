package com.hzy.blog.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class Topic implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "topic_id")
    private String topicId;

    private String topicTypeId;

    private String userId;

    private String topicTitle;

    private LocalDateTime topicAddTime;

    private String topicContext;

    private Integer topicGoodNumber;

    private Integer topicLookNumber;

    private Integer topicHot;

    private Integer topicCollectionNumber;


}
