package com.hzy.blog.service;

import com.hzy.blog.entity.bean.ResultBean;

public interface IPushService {

    void pushToOne(String uid, String text);

    void pushToAll(String text);

    ResultBean pushMessageToXFServer(String uid, String text);


}
