package com.hzy.blog.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzy
 * @since 2024-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CommentT implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "comment_t_id")
    private String commentTId;

    private String topicId;

    private String userId;

    private String commentTContent;

    private LocalDateTime commentTTime;

    private Integer commentTGoodNumber;


}
