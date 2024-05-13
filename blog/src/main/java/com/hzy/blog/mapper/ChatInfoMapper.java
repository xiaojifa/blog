package com.hzy.blog.mapper;

import com.hzy.blog.entity.ChatInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/5/13 11:01
 */
public interface ChatInfoMapper {

    /**
     * 新增
     */
    int insert(ChatInfo chatInfo);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(ChatInfo chatInfo);

    /**
     * 根据ID查询
     */
    ChatInfo selectById(Integer id);

    /**
     * 查询所有
     */
    List<ChatInfo> selectAll(ChatInfo chatInfo);

    @Select("select count(*) from chat_info where user_id = #{chatUserId} and chat_user_id = #{userId} and isread = '否'")
    Integer selectUnReadChatNum(@Param("userId") Integer userId, @Param("chatUserId") Integer chatUserId);

    List<ChatInfo> selectUserChat(@Param("userId") Integer userId, @Param("chatUserId") Integer chatUserId);

    @Update("update chat_info set isread = '是' where user_id = #{chatUserId} and chat_user_id = #{userId}")
    void updateRead(@Param("userId") Integer userId, @Param("chatUserId") Integer chatUserId);
}
