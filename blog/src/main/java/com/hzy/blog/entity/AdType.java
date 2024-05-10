package com.hzy.blog.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class AdType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 广告类型id
     */
    @TableId(value = "ad_type_id")
    private String adTypeId;

    /**
     * 广告类型名称
     */
    @TableField(value = "ad_type_title")
    private String adTypeTitle;

    /**
     * 广告标识
     */
    @TableField(value = "ad_type_tag")
    private String adTypeTag;

    /**
     * 广告类型排序
     */
    @TableField(value = "ad_type_sort")
    private Integer adTypeSort;

    /**
     * 广告类型添加时间
     */
    @TableField(value = "ad_type_add_time")
    private Date adTypeAddTime;


}
