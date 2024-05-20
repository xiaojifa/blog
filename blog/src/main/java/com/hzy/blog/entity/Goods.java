package com.hzy.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuyu
 * @date 2024/5/17
 */
@Data
public class Goods {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer store;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date updateTime;
    private String unit;
}
