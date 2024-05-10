package com.hzy.blog.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzy
 * @since 2024-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Link implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 友情连接id
     */
    @TableId(value = "link_id")
    private String linkId;

    /**
     * 连接标题
     */
    private String linkTitle;

    /**
     * 友情连接地址
     */
    private String linkUrl;

    /**
     * 友情连接logo
     */
    private String linkLogoUrl;

    /**
     * 排序
     */
    private Integer linkSort;

    /**
     * 添加友情连接的时间
     */
    private LocalDateTime linkAddTime;


}
