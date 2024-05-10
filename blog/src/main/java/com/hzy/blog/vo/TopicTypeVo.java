package com.hzy.blog.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/7 19:39
 */
@Data
public class TopicTypeVo {

    /**
     * 话题分类id
     */
    private String topicTypeId;

    /**
     * 话题分类父id
     */
    private String topicTypeParentId;

    /**
     * 话题分类名称
     */
    private String topicTypeName;

    /**
     * 话题分类排序，越小越靠前
     */
    private Integer topicTypeSort;

    /**
     * 添加时间
     */
    private Date topicTypeAddTime;

    /**
     * 话题数量
     */
    @TableField(exist = false)
    private Integer topicCount;
    
    
}
