package com.hzy.blog.controller;

import lombok.Data;

/**
 * @author liuyu
 * @date 2024/5/17
 */
@Data
public class AliPay {
    private String traceNo;
    private double totalAmount;
    private String subject;
    private String alipayTraceNo;
}
