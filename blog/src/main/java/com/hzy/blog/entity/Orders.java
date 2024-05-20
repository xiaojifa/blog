package com.hzy.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuyu
 * @date 2024/5/17
 */
@Data
public class Orders {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer goodsId;
    private String name;
    private String orderId;
    private String alipayNo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date payTime;
    private String state;
    private BigDecimal total;
}
