package com.hzy.blog.controller;

import com.hzy.blog.entity.bean.ResultBean;
import com.hzy.blog.service.IPushService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/17 14:55
 */
@Slf4j
@RestController
@RequestMapping("/xfModel")
public class TestController {

    @Resource
    private IPushService pushService;

    @GetMapping("/test")
    public ResultBean test(String uid, String text) {
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(text)) {
            log.error("uid或text不能为空");
            return ResultBean.fail("uid或text不能为空");
        }
        return pushService.pushMessageToXFServer(uid, text);
    }

}
