package com.hzy.blog.service.impl;

import com.hzy.blog.entity.ChatGroup;
import com.hzy.blog.mapper.ChatGroupMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/13 10:55
 */
@Service
public class ChatGroupServiceImpl {

    @Resource
    private ChatGroupMapper chatGroupMapper;
    @Resource
    private ChatInfoServiceImpl chatInfoServiceImpl;
    @Resource
    private UserServiceImpl userServiceImpl;

    /**
     * 新增
     */
    public void add(ChatGroup chatGroup) {
        ChatGroup dbChatGroup = chatGroupMapper.selectByChatUserIdAndUserId(chatGroup.getChatUserId(), chatGroup.getUserId());
        if (dbChatGroup == null) {
            chatGroupMapper.insert(chatGroup);
        }
        ChatGroup dbChatGroup1 = chatGroupMapper.selectByChatUserIdAndUserId(chatGroup.getUserId(), chatGroup.getChatUserId());
        if (dbChatGroup1 == null) {
            dbChatGroup1 = new ChatGroup();
            dbChatGroup1.setChatUserId(chatGroup.getUserId());
            dbChatGroup1.setUserId(chatGroup.getChatUserId());
            chatGroupMapper.insert(dbChatGroup1);
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        chatGroupMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            chatGroupMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(ChatGroup chatGroup) {
        chatGroupMapper.updateById(chatGroup);
    }

    /**
     * 根据ID查询
     */
    public ChatGroup selectById(Integer id) {
        return chatGroupMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<ChatGroup> selectAll(ChatGroup chatGroup) {
        return chatGroupMapper.selectAll(chatGroup);
    }

}
