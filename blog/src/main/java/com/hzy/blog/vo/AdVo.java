package com.hzy.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/26 20:20
 */
@Data
public class AdVo {

    /**
     * 广告id
     */
    private String adId;

    /**
     * 广告类型
     */
    private String adTypeId;

    /**
     * 广告标题
     */
    private String adTitle;

    /**
     * 广告的图片url地址
     */
    private String adImgUrl;

    /**
     * 广告跳转连接
     */
    private String adLinkUrl;

    /**
     * 广告排序，越小越排在前面
     */
    private Integer adSort;

    /**
     * 广告开始时间
     */
    private Date adBeginTime;

    /**
     * 广告结束时间
     */
    private Date adEndTime;

    /**
     * 添加广告的时间
     */
    private Date adAddTime;

    /**
     * 广告类型名称
     */
    private String adTypeTitle;

}
