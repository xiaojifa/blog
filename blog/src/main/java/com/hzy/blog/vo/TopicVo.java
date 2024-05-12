package com.hzy.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/7 15:06
 */
@Data
public class TopicVo {
    /**
     * 话题id
     */
    private String topicId;

    /**
     * 话题分类id
     */
    private String topicTypeId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户是否是VIP
     */
    private Integer userVip;

    /**
     * 用户是否冻结
     */
    private Integer userFrozen;

    /**
     * 用户注册时间
     */
    private LocalDateTime userRegisterTime;

    /**
     * 话题标题
     */
    private String topicTitle;

    /**
     * 话题添加时间
     */
    private LocalDateTime topicAddTime;

    /**
     * 是否是热门话题 0否，1是
     */
    private Integer topicHot;

    /**
     * 点赞次数
     */
    private Integer topicGoodNumber;

    /**
     * 观看次数
     */
    private Integer topicLookNumber;

    /**
     * 收藏次数
     */
    private Integer topicCollectionNumber;

    /**
     * 话题类型名称
     */
    private String topicTypeName;

    /**
     * 话题内容
     */
    private String topicContext;


}
