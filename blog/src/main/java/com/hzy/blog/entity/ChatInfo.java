package com.hzy.blog.entity;

import lombok.Data;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/13 10:38
 */
@Data
public class ChatInfo {

    /** ID */
    private Integer id;

    /** 聊天用户ID */
    private Integer chatUserId;

    /** 当前用户ID */
    private Integer userId;

    /** 聊天内容 */
    private String text;

    /** 是否已读 */
    private String isread;

    /** 时间 */
    private String time;

    private String chatUserName;

    private String chatUserAvatar;

    private String userName;

    private String userAvatar;


}
