package com.hzy.blog.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzy.blog.dto.topic.PublishTopicActionDto;
import com.hzy.blog.entity.*;
import com.hzy.blog.exception.CommonException;
import com.hzy.blog.mapper.TopicMapper;
import com.hzy.blog.service.*;
import com.hzy.blog.utils.CommonResult;
import com.hzy.blog.vo.TopicVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hzy
 * @since 2024-05-07
 */
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements ITopicService {

    @Resource
    private TopicMapper topicMapper;
    @Resource
    private ITopicTagListService topicTagListService;
    @Resource
    private ICommentTService commentService;
    @Resource
    private ICommentTReplyService commentReplyService;
    @Resource
    private ServletContext servletContext;
    @Resource
    private IUserCollectionTopicService userCollectionTopicService;
    @Resource
    private IUploadFileListService uploadFileListService;
    /**
     * 话题列表
     *
     * @param topicPage
     * @param topicTitle
     * @return
     */
    @Override
    public IPage<TopicVo> topicList(IPage<TopicVo> topicPage, String topicTitle, String userId) {
        return topicMapper.topicList(topicPage, topicTitle, userId);
    }

    /**
     * 话题列表 前端
     *
     * @param topicPage
     * @param topicTitle
     * @param topicTypeId
     * @return
     */
    @Override
    public IPage<TopicVo> topicListView(Page<TopicVo> topicPage, String topicTitle, String topicTypeId) {
        return topicMapper.topicListView(topicPage, topicTitle, topicTypeId);
    }

    /**
     * 发布话题方法
     *
     * @param publishTopicActionDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult publishTopicAction(HttpServletRequest request, PublishTopicActionDto publishTopicActionDto) {
        //保存话题
        User user = (User) request.getSession().getAttribute("user");
        Topic topic = new Topic();
        topic.setTopicId(publishTopicActionDto.getTopicId());
        topic.setTopicTypeId(publishTopicActionDto.getTopicTypeId());
        topic.setUserId(user.getUserId());
        topic.setTopicTitle(publishTopicActionDto.getTopicTitle());
        if (StrUtil.isBlank(topic.getTopicId())) {
            topic.setTopicAddTime(LocalDateTime.now());
        }
        topic.setTopicContext(publishTopicActionDto.getTopicContext());
        topic.setTopicGoodNumber(0);
        topic.setTopicLookNumber(0);
        topic.setTopicHot(0);
        topic.setTopicCollectionNumber(0);

        if (!saveOrUpdate(topic)) {
            return CommonResult.failed("操作失败，请刷新页面重试!");
        }

        //保持话题的标签
        String[] topicTagIds = publishTopicActionDto.getTopicTagIds();
        if (Objects.nonNull(topicTagIds) && topicTagIds.length > 0) {
            //删除原先的标签数据
            topicTagListService.remove(Wrappers.<TopicTagList>lambdaQuery().eq(TopicTagList::getTopicId, topic.getTopicId()));
        }

        ArrayList<TopicTagList> topicTagLists = new ArrayList<>();
        for (String topicTagId : topicTagIds) {
            TopicTagList topicTagList = new TopicTagList();
            topicTagList.setTopicId(topic.getTopicId());
            topicTagList.setTopicTagId(topicTagId);
            topicTagLists.add(topicTagList);
        }
        if (!topicTagListService.saveBatch(topicTagLists, 50)) {
            throw new CommonException("操作话题失败，保存话题标签失败");
        }

        servletContext.removeAttribute("indexTopicList");

        return CommonResult.success("操作成功");
    }

    /**
     * 删除话题
     *
     * @param topicId
     * @return
     */
    @Override
    public CommonResult delTopic(String topicId) {

        if (StrUtil.isBlank(topicId)) {
            return CommonResult.failed("删除失败，参数不正确，请刷新页面重试");
        }
        Topic topic = getById(topicId);
        if (Objects.isNull(topic)) {
            return CommonResult.failed("删除失败，可能该话题已经被删除");
        }

        //删除话题
        if (!removeById(topicId)) {
            return CommonResult.failed("删除失败，可能该话题已经被删除");
        }
        //删除话题评论
        List<CommentT> commentTList = commentService.list(Wrappers.<CommentT>lambdaQuery()
                .eq(CommentT::getTopicId, topicId)
                .select(CommentT::getCommentTId));
        if (CollUtil.isNotEmpty(commentTList)) {
            List<String> commentIdList = commentTList.stream().map(CommentT::getCommentTId).collect(Collectors.toList());
            commentService.removeByIds(commentIdList);
            commentReplyService.remove(Wrappers.<CommentTReply>lambdaQuery().in(CommentTReply::getCommentTId, commentIdList));
        }
        //删除话题对应的标签
        topicTagListService.remove(Wrappers.<TopicTagList>lambdaQuery().eq(TopicTagList::getTopicId, topicId));

        //删除用户收藏的话题
        userCollectionTopicService.remove(Wrappers.<UserCollectionTopic>lambdaQuery().eq(UserCollectionTopic::getTopicId, topicId));

        servletContext.removeAttribute("topicTypeList");
        return CommonResult.success("删除成功！");
    }

    /**
     * 首页最新话题
     *
     * @return
     */
    @Override
    public List<TopicVo> getIndexTopicList() {
        return topicMapper.getIndexTopicList();
    }

    /**
     * 根据话题id获取话题信息
     *
     * @param topicId
     * @return
     */
    @Override
    public TopicVo getTopic(String topicId) {
        return topicMapper.getTopic(topicId);
    }

    /**
     * 收藏话题
     *
     * @param user
     * @param topicId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult topicCollection(User user, String topicId) {

        if (userCollectionTopicService.count(Wrappers.<UserCollectionTopic>lambdaQuery()
                .eq(UserCollectionTopic::getUserId, user.getUserId())
                .eq(UserCollectionTopic::getTopicId, topicId)) > 0) {
            return CommonResult.failed("客官！该话题您已经收藏了，请到个人中心查看哦");
        }

        UserCollectionTopic userCollectionTopic = new UserCollectionTopic();
        userCollectionTopic.setUserId(user.getUserId());
        userCollectionTopic.setTopicId(topicId);
        userCollectionTopic.setUserCollectionTopicTime(DateUtil.date());
        if (!userCollectionTopicService.save(userCollectionTopic)) {
            return CommonResult.failed("收藏失败啦，刷新页面重试");
        }

        //添加收藏次数
        Topic topic = getById(topicId);
        if (Objects.nonNull(topic)) {
            Integer topicCollectionNumber = topic.getTopicCollectionNumber();
            ++topicCollectionNumber;
            topic.setTopicCollectionNumber(topicCollectionNumber);
            if (!updateById(topic)) {
                throw new CommonException("收藏失败");
            }
        }
        return CommonResult.success("恭喜，收藏成功，客官可以到个人中心查看");
    }

    /**
     * 获取标签对应的话题列表
     *
     * @param topicPage
     * @param topicTagId
     * @return
     */
    @Override
    public IPage<TopicVo> tagTopicList(Page<TopicVo> topicPage, String topicTagId) {
        return topicMapper.tagTopicList(topicPage, topicTagId);
    }

}
