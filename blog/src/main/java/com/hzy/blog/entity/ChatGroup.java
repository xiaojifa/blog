package com.hzy.blog.entity;

import lombok.Data;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/13 10:26
 */
@Data
public class ChatGroup {

    /** ID */
    private Integer id;

    /** 聊天用户ID */
    private Integer chatUserId;

    /** 当前用户ID */
    private Integer userId;

    private String chatUserName;

    private String chatUserAvatar;

    private Integer chatNum;


}
