package com.hzy.blog.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/14 16:24
 */

@Data
public class ArticleTypeVo {

    /**
     * 文章分类id
     */
    private String articleTypeId;

    /**
     * 文章分类父id
     */
    private String articleTypeParentId;

    /**
     * 文章分类名称
     */
    private String articleTypeName;

    /**
     * 文章分类排序，越小越靠前
     */
    private Integer articleTypeSort;

    /**
     * 添加时间
     */
    private Date articleTypeAddTime;

    /**
     * 文章数量
     */
    @TableField(exist = false)
    private Integer articleCount;

}
