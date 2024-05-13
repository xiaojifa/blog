package com.hzy.blog.entity;

import lombok.Data;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/13 11:07
 */
@Data
public class Account {

    private Integer id;
    /** 用户名 */
    private String username;
    /** 名称 */
    private String name;
    /** 密码 */
    private String password;
    /** 角色标识 */
    private String role;
    /** 新密码 */
    private String newPassword;
    /** 头像 */
    private String avatar;

    private String token;


}
