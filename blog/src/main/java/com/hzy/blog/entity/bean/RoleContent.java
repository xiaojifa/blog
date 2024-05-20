package com.hzy.blog.entity.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/17 14:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleContent {

    public static final String ROLE_USER = "user";

    public static final String ROLE_ASSISTANT = "assistant";

    private String role;

    private String content;

    public static RoleContent createUserRoleContent(String content) {
        return new RoleContent(ROLE_USER, content);
    }

    public static RoleContent createAssistantRoleContent(String content) {
        return new RoleContent(ROLE_ASSISTANT, content);
    }


}
