package com.hzy.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.hzy.blog.config.XFConfig;
import com.hzy.blog.entity.bean.NettyGroup;
import com.hzy.blog.entity.bean.ResultBean;
import com.hzy.blog.entity.bean.RoleContent;
import com.hzy.blog.listener.XFWebClient;
import com.hzy.blog.listener.XFWebSocketListener;
import com.hzy.blog.service.IPushService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/17 14:38
 */
@Slf4j
@Service
public class PushServiceImpl implements IPushService {

    @Resource
    private XFConfig xfConfig;

    @Resource
    private XFWebClient xfWebClient;

    @Override
    public void pushToOne(String uid, String text) {
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(text)) {
            log.error("uid或text均不能为空");
            throw new RuntimeException("uid或text均不能为空");
        }
        ConcurrentHashMap<String, Channel> userChannelMap = NettyGroup.getUserChannelMap();
        for (String channelId : userChannelMap.keySet()) {
            if (channelId.equals(uid)) {
                Channel channel = userChannelMap.get(channelId);
                if (channel != null) {
                    ResultBean success = ResultBean.success(text);
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(success)));
                    log.info("信息发送成功：{}", JSON.toJSONString(success));
                } else {
                    log.error("该id对于channelId不存在！");
                }
                return;
            }
        }
        log.error("该用户不存在！");
    }

    @Override
    public void pushToAll(String text) {
        String trim = text.trim();
        ResultBean success = ResultBean.success(trim);
        NettyGroup.getChannelGroup().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(success)));
        log.info("信息推送成功：{}", JSON.toJSONString(success));
    }

    //测试账号只有2个并发，此处只使用一个，若是生产环境允许多个并发，可以采用分布式锁
    @Override
    public synchronized ResultBean pushMessageToXFServer(String uid, String text) {
        RoleContent userRoleContent = RoleContent.createUserRoleContent(text);
        ArrayList<RoleContent> questions = new ArrayList<>();
        questions.add(userRoleContent);
        XFWebSocketListener xfWebSocketListener = new XFWebSocketListener();
        WebSocket webSocket = xfWebClient.sendMsg(uid, questions, xfWebSocketListener);
        if (webSocket == null) {
            log.error("webSocket连接异常");
            ResultBean.fail("请求异常，请联系管理员");
        }
        try {
            int count = 0;
            int maxCount = xfConfig.getMaxResponseTime() * 5;
            while (count <= maxCount) {
                Thread.sleep(200);
                if (xfWebSocketListener.isWsCloseFlag()) {
                    break;
                }
                count++;
            }
            if (count > maxCount) {
                return ResultBean.fail("响应超时，请联系相关人员");
            }
            return ResultBean.success(xfWebSocketListener.getAnswer());
        } catch (Exception e) {
            log.error("请求异常：{}", e);
        } finally {
            webSocket.close(1000, "");
        }
        return ResultBean.success("");
    }


}
