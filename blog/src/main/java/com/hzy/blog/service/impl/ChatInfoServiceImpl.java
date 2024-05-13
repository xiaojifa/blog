package com.hzy.blog.service.impl;

import cn.hutool.core.date.DateUtil;
import com.hzy.blog.entity.Account;
import com.hzy.blog.entity.ChatInfo;
import com.hzy.blog.mapper.ChatInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/13 10:59
 */
@Service
public class ChatInfoServiceImpl {

    @Resource
    private ChatInfoMapper chatInfoMapper;

    /**
     * 新增
     */
    public void add(ChatInfo chatInfo) {
        chatInfo.setTime(DateUtil.now());
        chatInfoMapper.insert(chatInfo);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        chatInfoMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            chatInfoMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(ChatInfo chatInfo) {
        chatInfoMapper.updateById(chatInfo);
    }

    /**
     * 根据ID查询
     */
    public ChatInfo selectById(Integer id) {
        return chatInfoMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<ChatInfo> selectAll(ChatInfo chatInfo) {
        return chatInfoMapper.selectAll(chatInfo);
    }

    public Integer selectUnReadChatNum(Integer userId, Integer chatUserId) {
        return chatInfoMapper.selectUnReadChatNum(userId, chatUserId);
    }

}
