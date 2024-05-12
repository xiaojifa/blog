package com.hzy.blog.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/12 1:44
 */
@Data
public class UserVo {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

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
