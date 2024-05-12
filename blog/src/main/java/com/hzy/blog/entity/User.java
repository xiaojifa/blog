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
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "user_id")
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 是否可以发布文章 0不能，1可以发布
     */
    private Integer userPublishArticle;

    /**
     * 是否可以发布话题 0不能，1可以发布
     */
    private Integer userPublishTopic;

    /**
     * 是否是VIP
     */
    private Integer userVip;

    /**
     * 是否冻结
     */
    private Integer userFrozen;

    /**
     * 注册时间
     */
    private LocalDateTime userRegisterTime;

}
