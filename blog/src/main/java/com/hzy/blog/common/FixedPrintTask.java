package com.hzy.blog.common;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

/**
 * @author 14439
 */
@Component
public class FixedPrintTask {
    @Resource
    private ServletContext servletContext;

    /**
     * 每天凌晨4点删除首页缓存
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void execute() {
        servletContext.removeAttribute("articleTypeList");
        servletContext.removeAttribute("articleHotList");
        servletContext.removeAttribute("articleTagList");
        servletContext.removeAttribute("adIndexList");
        servletContext.removeAttribute("linkList");
    }

}