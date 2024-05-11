package com.hzy.blog.dto.ad;

import lombok.Data;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/26 15:44
 */
@Data
public class AdDto {

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
    private String adBeginTime;

    /**
     * 广告结束时间
     */
    private String adEndTime;
}
