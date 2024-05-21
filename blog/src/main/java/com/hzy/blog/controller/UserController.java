package com.hzy.blog.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzy.blog.dto.article.PublishArticleActionDto;
import com.hzy.blog.dto.topic.PublishTopicActionDto;
import com.hzy.blog.dto.user.UserDto;
import com.hzy.blog.entity.*;
import com.hzy.blog.service.*;
import com.hzy.blog.utils.CommonPage;
import com.hzy.blog.utils.CommonResult;
import com.hzy.blog.vo.ArticleVo;
import com.hzy.blog.vo.CommentTVo;
import com.hzy.blog.vo.CommentVo;
import com.hzy.blog.vo.TopicVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/14 17:19
 */
@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserCollectionArticleService userCollectionArticleService;
    @Resource
    private IArticleService articleService;
    @Resource
    private IArticleTypeService articleTypeService;
    @Resource
    private IArticleTagService articleTagService;
    @Resource
    private IArticleTagListService articleTagListService;
    @Resource
    private IUploadFileListService uploadFileListService;
    @Resource
    private ICommentService commentService;
    @Resource
    private ICommentTService commentTService;
    @Resource
    private IUserService userService;
    @Resource
    private ICommentReplyService commentReplyService;
    @Resource
    private ICommentTReplyService commentTReplyService;
    @Resource
    private IUserCollectionTopicService userCollectionTopicService;
    @Resource
    private ITopicService topicService;
    @Resource
    private ITopicTypeService topicTypeService;
    @Resource
    private ITopicTagService topicTagService;
    @Resource
    private ITopicTagListService topicTagListService;
    @Resource
    private IPushService pushService;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadFile")
    @ResponseBody
    public String uploadFile(HttpServletRequest request, MultipartFile file) {

        // 检查文件是否为空，如果为空则返回null
        if (file.isEmpty()) {
            return null;
        }

        // 从HttpServletRequest对象中获取用户信息
        User user = (User) request.getSession().getAttribute("user");
        // 检查用户是否有发布文章的权限
        if (Objects.isNull(user.getUserPublishArticle()) || user.getUserPublishArticle() != 1) {
            return null;
        }

        // 调用uploadFileListService的getUploadFileUrl方法获取文件的URL，并返回该URL
        return uploadFileListService.getUploadFileUrl(file);
    }

//    @GetMapping("/test")
//    public ResultBean test(String uid, String text) {
//        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(text)) {
//            log.error("uid或text不能为空");
//            return ResultBean.fail("uid或text不能为空");
//        }
//        return pushService.pushMessageToXFServer(uid, text);
//    }

    /**
     * 用户管理页面
     *
     * @return
     */
    @GetMapping("/manager")
    public String userManager() {
        return "/user/userManager";
    }
    @GetMapping("/managers")
    public String userManager(HttpServletRequest request) {
        userUpdate(request);
        return "/user/userManager";
    }

    /**
     * 用户修改VIP
     *
     *
     */
    public CommonResult userUpdate(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user= (User) session.getAttribute("user");
       user.setUserVip(1);
        if (userService.updateById(user)) {
            return CommonResult.success("修改成功");
        }

        return CommonResult.failed("修改失败，请重试");
    }

    /**
     * 用户收藏话题
     *
     * @param pageNumber
     * @return
     */
    @GetMapping("/collectionTopic/list")
    public String collectionTopicList(HttpServletRequest request, Integer pageNumber, Model model) {
        // 从HttpServletRequest对象中获取用户信息
        User user = (User) request.getSession().getAttribute("user");
        // 创建一个Page对象来分页查询话题
        Page<Topic> topicPage = new Page<>(pageNumber, 24);
        // 调用userCollectionTopicService的list方法获取用户收藏的话题ID列表，并将其转换为List类型
        List<String> topicIdList = userCollectionTopicService.list(Wrappers.<UserCollectionTopic>lambdaQuery()
                        .eq(UserCollectionTopic::getUserId, user.getUserId())
                        .select(UserCollectionTopic::getTopicId)).stream()
                .map(UserCollectionTopic::getTopicId)
                .collect(Collectors.toList());
        // 如果该列表不为空，则使用LambdaQueryWrapper构建查询条件(LambdaQueryWrapper是Mybatis-Plus中的一个查询条件构造器，用于构建查询条件)
        if (CollUtil.isNotEmpty(topicIdList)) {
            LambdaQueryWrapper<Topic> wrapper = Wrappers.<Topic>lambdaQuery()
                    // 查询话题的相关信息
                    .in(Topic::getTopicId, topicIdList)
                    .select(Topic::getTopicId,
                            Topic::getTopicAddTime,
                            Topic::getTopicCollectionNumber,
                            Topic::getTopicGoodNumber,
                            Topic::getTopicLookNumber,
                            Topic::getTopicTitle);
            IPage<Topic> topicIPage = topicService.page(topicPage, wrapper);
            // 将结果添加到Model对象中user_collection_article
            model.addAttribute("topicIPage", CommonPage.restPage(topicIPage));
        }

        // 返回"/user/collectionList"视图名称
        return "/user/collectionList";
    }

    /**
     * 用户收藏
     *
     * @param pageNumber
     * @return
     */
    @GetMapping("/topic/list")
    public String topicList(HttpServletRequest request, Integer pageNumber, Model model) {
        // 从HttpServletRequest对象中获取用户信息
        User user = (User) request.getSession().getAttribute("user");
        // 创建一个Page对象来分页查询话题
        Page<TopicVo> topicPage = new Page<>(pageNumber, 24);
        // 调用topicService的topicList方法获取话题列表
        IPage<TopicVo> topicVoIPage = topicService.topicList(topicPage, null, null);
        // 将结果添加到Model对象中
        model.addAttribute("topicVoIPage", CommonPage.restPage(topicVoIPage));
        // 返回"/user/topicList"视图名称
        return "/user/topicList";
    }

    /**
     * 发布话题
     *
     * @return
     */
    @GetMapping("/publishTopic")
    public String releaseTopic(HttpServletRequest request, Model model, String topicId) {
        // 从HttpServletRequest对象中获取用户信息
        User user = (User) request.getSession().getAttribute("user");
        // 判断用户是否有发布话题的权限且不为空
        if (Objects.isNull(user) || Objects.isNull(user.getUserPublishTopic()) || user.getUserPublishTopic() != 1) {
            // 没有权限，则重定向到首页
            return "redirect:/";
        }

        // 根据话题ID查询话题信息
        Topic topic = topicService.getOne(Wrappers.<Topic>lambdaQuery()
                .eq(Topic::getUserId, user.getUserId())
                .eq(Topic::getTopicId, topicId));
        // 不为空，将结果添加到Model对象中
        if (Objects.nonNull(topic)) {
            model.addAttribute("topic", topic);

            //获取文章标签
            List<TopicTagList> topicTagLists = topicTagListService
                    .list(Wrappers.<TopicTagList>lambdaQuery()
                            .eq(TopicTagList::getTopicId, topic.getTopicId())
                            .select(TopicTagList::getTopicTagId));

            if (CollUtil.isNotEmpty(topicTagLists)) {
                List<String> topicTagIdList = topicTagLists.stream()
                        .map(TopicTagList::getTopicTagId)
                        .collect(Collectors.toList());
                model.addAttribute("topicTagIdList", topicTagIdList);
            }

            //获取该文章类型的同级类型
            String topicTypeId = topic.getTopicTypeId();
            List<TopicType> topicSameTypeList = topicTypeService
                    .list(Wrappers.<TopicType>lambdaQuery()
                            .eq(TopicType::getTopicTypeParentId, topicTypeService.getById(topicTypeId).getTopicTypeParentId()));
            model.addAttribute("topicSameTypeList", topicSameTypeList);

            //获取该文章上级类型
            model.addAttribute("topicTypeParentId", topicTypeService
                    .getById(topic.getTopicTypeId())
                    .getTopicTypeParentId());
        }

        //获取类型
        List<TopicType> topicType0List = topicTypeService
                .list(Wrappers.<TopicType>lambdaQuery()
                        .isNull(TopicType::getTopicTypeParentId)
                        .or()
                        .eq(TopicType::getTopicTypeParentId, "")
                        .orderByAsc(TopicType::getTopicTypeSort));
        model.addAttribute("topicType0List", topicType0List);

        //获取标签
        List<TopicTag> topicTagList = topicTagService.list();
        model.addAttribute("topicTagList", topicTagList);

        return "/user/publishTopic";
    }

    /**
     * 获取二级话题分类
     *
     * @param topicTypeId
     * @return
     */
    @PostMapping("/getTopicTypeChild")
    @ResponseBody
    public CommonResult getTopicTypeChild(String topicTypeId) {
        // 接收一个一级话题类型ID作为参数
        if (StrUtil.isBlank(topicTypeId)) {
            return CommonResult.failed("请选择一级分类");
        }
        // 返回该一级话题类型下的所有二级话题类型列表
        List<TopicType> topicTypeList = topicTypeService.list(Wrappers.<TopicType>lambdaQuery()
                .eq(TopicType::getTopicTypeParentId, topicTypeId)
                .select(TopicType::getTopicTypeId, TopicType::getTopicTypeName));

        // 返回成功的结果，并将二级话题类型列表作为数据返回
        return CommonResult.success(topicTypeList);
    }

    /**
     * 发布话题方法
     *
     * @param publishTopicActionDto
     * @return
     */
    @PostMapping("/publishTopicAction")
    @ResponseBody
    public CommonResult publishTopicAction(HttpServletRequest request, @Valid PublishTopicActionDto publishTopicActionDto, MultipartFile topicCoverFile) throws IOException {
        HttpSession session = request.getSession();
        // 从HttpServletRequest对象中获取用户信息
        User user = (User) session.getAttribute("user");
        // 判断用户是否登录过期
        if (Objects.isNull(user)){
            return CommonResult.failed("登录过期，请重新登录");
        }
        // 获取用户信息
        User serviceById = userService.getById(user.getUserId());
        if(Objects.isNull(serviceById)){
            // 将其存储在session中
            session.setAttribute("user",serviceById);
        }
        // 判断用户是否有发布话题的权限
        if (Objects.isNull(serviceById.getUserPublishTopic()) || serviceById.getUserPublishTopic() != 1) {
            return CommonResult.failed("当前您还没有权限发布，请联系管理员");
        }
        if (Objects.nonNull(topicCoverFile)) {
            //判断是否上传的图片，是否是我们指定的像素
            BufferedImage read = ImageIO.read(topicCoverFile.getInputStream());
            if (Objects.isNull(read)) {
                return CommonResult.failed("请上传图片文件");
            }
            int width = read.getWidth();
            int height = read.getHeight();
            if (width != 250 || height != 170) {
                return CommonResult.failed("图片的像素为 250px * 170px");
            }
            // 将上传的图片文件的URL设置到PublishTopicActionDto对象中
            publishTopicActionDto.setTopicCoverUrl(uploadFileListService.getUploadFileUrl(topicCoverFile));
        }
        // 调用topicService的publishTopicAction方法发布话题
        return topicService.publishTopicAction(request, publishTopicActionDto);
    }

    /**
     * 用户收藏文章
     *
     * @param pageNumber
     * @return
     */
    @GetMapping("/collection/list")
    public String collectionList(HttpServletRequest request, Integer pageNumber, Model model) {
        // 从HttpServletRequest对象中获取用户信息
        User user = (User) request.getSession().getAttribute("user");
        // 创建一个Page对象来分页查询文章
        Page<Article> articlePage = new Page<>(pageNumber, 24);
        // 调用userCollectionArticleService的list方法获取用户收藏的文章ID列表，并将其转换为List类型
        List<String> articleIdList = userCollectionArticleService.list(Wrappers.<UserCollectionArticle>lambdaQuery()
                        .eq(UserCollectionArticle::getUserId, user.getUserId())
                        .select(UserCollectionArticle::getArticleId)).stream()
                .map(UserCollectionArticle::getArticleId)
                .collect(Collectors.toList());
        // 如果该列表不为空，则使用LambdaQueryWrapper构建查询条件
        if (CollUtil.isNotEmpty(articleIdList)) {
            LambdaQueryWrapper<Article> wrapper = Wrappers.<Article>lambdaQuery()
                    .in(Article::getArticleId, articleIdList)
                    .select(Article::getArticleId,
                            Article::getArticleAddTime,
                            Article::getArticleCollectionNumber,
                            Article::getArticleGoodNumber,
                            Article::getArticleLookNumber,
                            Article::getArticleCoverUrl,
                            Article::getArticleTitle);
            IPage<Article> articleIPage = articleService.page(articlePage, wrapper);
            model.addAttribute("articleIPage", CommonPage.restPage(articleIPage));
        }
        // 返回"/user/collectionList"视图名称
        return "/user/collectionList";
    }

    /**
     * 用户收藏
     *
     * @param pageNumber
     * @return
     */
    @GetMapping("/article/list")
    public String articleList(HttpServletRequest request, Integer pageNumber, Model model) {
        // 从HttpServletRequest对象中获取用户信息
        User user = (User) request.getSession().getAttribute("user");
        // 创建一个Page对象来分页查询文章
        Page<ArticleVo> articlePage = new Page<>(pageNumber, 24);
        // 调用articleService的articleList方法获取文章列表
        IPage<ArticleVo> articleVoIPage = articleService.articleList(articlePage, null, null);
        // 将结果添加到Model对象中
        model.addAttribute("articleVoIPage", CommonPage.restPage(articleVoIPage));
        return "/user/articleList";
    }


    /**
     * 发布文章
     *
     * @return
     */
    @GetMapping("/publishArticle")
    public String releaseArticle(HttpServletRequest request, Model model, String articleId) {
        User user = (User) request.getSession().getAttribute("user");
        if (Objects.isNull(user) || Objects.isNull(user.getUserPublishArticle()) || user.getUserPublishArticle() != 1) {
            return "redirect:/";
        }

        Article article = articleService.getOne(Wrappers.<Article>lambdaQuery().eq(Article::getUserId, user.getUserId()).eq(Article::getArticleId, articleId));
        if (Objects.nonNull(article)) {
            model.addAttribute("article", article);

            //获取文章标签
            List<ArticleTagList> articleTagLists = articleTagListService.list(Wrappers.<ArticleTagList>lambdaQuery()
                    .eq(ArticleTagList::getArticleId, article.getArticleId())
                    .select(ArticleTagList::getArticleTagId));

            if (CollUtil.isNotEmpty(articleTagLists)) {
                List<String> articleTagIdList = articleTagLists.stream().map(ArticleTagList::getArticleTagId).collect(Collectors.toList());
                model.addAttribute("articleTagIdList", articleTagIdList);
            }
            //获取该文章类型的同级类型
            String articleTypeId = article.getArticleTypeId();
            List<ArticleType> articleSameTypeList = articleTypeService.list(Wrappers.<ArticleType>lambdaQuery().eq(ArticleType::getArticleTypeParentId, articleTypeService.getById(articleTypeId).getArticleTypeParentId()));
            model.addAttribute("articleSameTypeList", articleSameTypeList);

            //获取该文章上级类型
            model.addAttribute("articleTypeParentId", articleTypeService.getById(article.getArticleTypeId()).getArticleTypeParentId());
        }

        //获取类型
        List<ArticleType> articleType0List = articleTypeService.list(Wrappers.<ArticleType>lambdaQuery().isNull(ArticleType::getArticleTypeParentId).or().eq(ArticleType::getArticleTypeParentId, "").orderByAsc(ArticleType::getArticleTypeSort));
        model.addAttribute("articleType0List", articleType0List);

        //获取标签
        List<ArticleTag> articleTagList = articleTagService.list();
        model.addAttribute("articleTagList", articleTagList);

        return "/user/publishArticle";
    }

    /**
     * 获取二级文章分类
     *
     * @param articleTypeId
     * @return
     */
    @PostMapping("/getArticleTypeChild")
    @ResponseBody
    public CommonResult getArticleTypeChild(String articleTypeId) {
        if (StrUtil.isBlank(articleTypeId)) {
            return CommonResult.failed("请选择一级分类");
        }

        List<ArticleType> articleTypeList = articleTypeService.list(Wrappers.<ArticleType>lambdaQuery()
                .eq(ArticleType::getArticleTypeParentId, articleTypeId)
                .select(ArticleType::getArticleTypeId, ArticleType::getArticleTypeName));

        return CommonResult.success(articleTypeList);
    }


    /**
     * 发布文章方法
     *
     * @param publishArticleActionDto
     * @return
     */
    @PostMapping("/publishArticleAction")
    @ResponseBody
    public CommonResult publishArticleAction(HttpServletRequest request, @Valid PublishArticleActionDto publishArticleActionDto, MultipartFile articleCoverFile) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (Objects.isNull(user)){
            return CommonResult.failed("登录过期，请重新登录");
        }
        User serviceById = userService.getById(user.getUserId());
        if(Objects.isNull(serviceById)){
            session.setAttribute("user", serviceById);
        }
        if (Objects.isNull(serviceById.getUserPublishArticle()) || serviceById.getUserPublishArticle() != 1) {
            return CommonResult.failed("当前您还没有权限发布，请联系管理员");
        }
        if (Objects.nonNull(articleCoverFile)) {
            publishArticleActionDto.setArticleCoverUrl(uploadFileListService.getUploadFileUrl(articleCoverFile));
        }
        return articleService.publishArticleAction(request, publishArticleActionDto);
    }

    /**
     * 个人中心，我的话题
     *
     * @param pageNumber
     * @param model
     * @return
     */
    @GetMapping("/myTopicList")
    public String myTopicList(HttpServletRequest request, Integer pageNumber, Model model) {
        // 从HttpServletRequest对象中获取用户信息
        User user = (User) request.getSession().getAttribute("user");
        // 判断页数为空或小于1
        if (Objects.isNull(pageNumber) || pageNumber < 1) {
            // 赋值为1
            pageNumber = 1;
        }
        // 创建一个Page对象，设置每页显示24条数据
        Page<TopicVo> topicPage = new Page<>(pageNumber, 24);
        // 调用topicService的topicList方法获取用户发布的话题列表
        IPage<TopicVo> topicVoIPage = topicService.topicList(topicPage, null, user.getUserId());
        // 将话题列表添加到Model对象中
        model.addAttribute("topicVoIPage", CommonPage.restPage(topicVoIPage));

        return "/user/myTopicList";
    }

    /**
     * 个人中心，我的文章
     *
     * @param pageNumber
     * @param model
     * @return
     */
    @GetMapping("/myArticleList")
    public String myArticleList(HttpServletRequest request, Integer pageNumber, Model model) {
        // 从HttpServletRequest对象中获取用户信息
        User user = (User) request.getSession().getAttribute("user");
        if (Objects.isNull(pageNumber) || pageNumber < 1) {
            pageNumber = 1;
        }
        Page<ArticleVo> articlePage = new Page<>(pageNumber, 24);
        IPage<ArticleVo> articleVoIPage = articleService.articleList(articlePage, null, user.getUserId());
        model.addAttribute("articleVoIPage", CommonPage.restPage(articleVoIPage));

        return "/user/myArticleList";
    }

    /**
     * 删除话题
     *
     * @param topicId
     * @return
     */
    @PostMapping("/delTopic")
    @ResponseBody
    public CommonResult delTopic(String topicId) {
        // 调用topicService的delTopic方法删除该话题  返回CommonResult对象，表示删除操作的结果
        return topicService.delTopic(topicId);
    }

    /**
     * 删除文章
     *
     * @param articleId
     * @return
     */
    @PostMapping("/delArticle")
    @ResponseBody
    public CommonResult delArticle(String articleId) {
        return articleService.delArticle(articleId);
    }

    /**
     * 用户评论话题
     *
     * @param request
     * @param topicId
     * @param commentTContent
     * @return
     */
    @PostMapping("/saveCommentT")
    @ResponseBody
    public CommonResult userSaveCommentT(HttpServletRequest request, String topicId, String commentTContent,String commentTId) {
        // 判断话题ID和评论内容是否为空
        if (StrUtil.isBlank(topicId) || StrUtil.isBlank(commentTContent)) {
            return CommonResult.failed("评论失败，请刷新页面重试");
        }
        // 判断评论内容的长度是否在1-800个字符之间
        if (commentTContent.isEmpty() || commentTContent.length() > 800) {
            return CommonResult.failed("评论内容在1-800个字符之间！");
        }
        // 从HttpServletRequest对象中获取用户信息
        User user = (User) request.getSession().getAttribute("user");
        // 判断用户是否登录过期
        if (Objects.isNull(user)) {
            return CommonResult.failed("客官！您的登录过期，请从新登录哦");
        }
        String userId = user.getUserId();

        // 创建一个CommentT对象，设置话题ID、用户ID、评论内容、评论时间和点赞数
        CommentT commentT1 = new CommentT();
        commentT1.setTopicId(topicId);
        commentT1.setUserId(userId);
        commentT1.setCommentTContent(commentTContent);
        commentT1.setCommentTTime(LocalDateTime.now());
        commentT1.setCommentTGoodNumber(0);

        // 调用commentTService的save方法保存评论信息
        if (commentTService.save(commentT1)) {
            CommentTVo commentTVo = new CommentTVo();
            BeanUtils.copyProperties(commentT1, commentTVo);
            commentTVo.setUserName(
                    userService.getOne(Wrappers.<User>lambdaQuery()
                                    .eq(User::getUserId, commentTVo.getUserId())
                                    .select(User::getUserName)).getUserName()
            );
            // 创建一个CommentTVo对象，将CommentT对象的属性复制到CommentTVo对象中  设置用户名和评论时间
            commentTVo.setCommentTTime(DateUtil.format(commentT1.getCommentTTime(),"yyyy-MM-dd HH:mm:ss"));
            // 返回成功的结果，并将CommentTVo对象作为数据返回
            return CommonResult.success(commentTVo);
        }
        return CommonResult.failed("评论失败");
    }

    /**
     * 用户评论文章
     *
     * @param request
     * @param articleId
     * @param commentContent
     * @return
     */
    @PostMapping("/saveComment")
    @ResponseBody
    public CommonResult userSaveComment(HttpServletRequest request, String articleId, String commentContent,String commentId) {

        // 检查文章ID和评论内容是否为空
        if (StrUtil.isBlank(articleId) || StrUtil.isBlank(commentContent)) {
            return CommonResult.failed("评论失败，请刷新页面重试");
        }

        // 检查评论内容的长度是否在1到800个
        if (commentContent.isEmpty() || commentContent.length() > 800) {
            return CommonResult.failed("评论内容在1-800个字符之间！");
        }

        // 从HttpServletRequest对象中获取用户信息
        User user = (User) request.getSession().getAttribute("user");
        if (Objects.isNull(user)) { // 是否为空
            return CommonResult.failed("您的登录过期，请从新登录哦");
        }
        String userId = user.getUserId();

        Comment comment1 = new Comment();
        comment1.setArticleId(articleId);
        comment1.setUserId(userId);
        comment1.setCommentContent(commentContent);
        comment1.setCommentTime(LocalDateTime.now());
        comment1.setCommentGoodNumber(0);

        // 调用commentService的save方法保存评论
        if (commentService.save(comment1)) {
            CommentVo commentVo = new CommentVo();
            // 如果保存成功，则将Comment对象转换为CommentVo对象
            BeanUtils.copyProperties(comment1, commentVo);
            // 设置用户名和评论时间
            commentVo.setUserName(
                    userService.getOne(Wrappers.<User>lambdaQuery()
                            .eq(User::getUserId, commentVo.getUserId())
                            .select(User::getUserName)).getUserName()
            );
            commentVo.setCommentTime(DateUtil.format(comment1.getCommentTime(),"yyyy-MM-dd HH:mm:ss"));
            return CommonResult.success(commentVo);
        }
        return CommonResult.failed("评论失败");
    }

//    /**
//     * 回复评论
//     * @param commentId
//     * @param articleId
//     * @param commentContent
//     * @return
//     */
//    @PostMapping("/commentReply")
//    @ResponseBody
//    public CommonResult commentReply(HttpServletRequest request,String commentId,String articleId,String commentContent){
//        if (StrUtil.isBlank(commentId) || StrUtil.isBlank(articleId) || StrUtil.isBlank(commentContent)) {
//            return CommonResult.failed("评论失败，请刷新页面重试");
//        }
//        if (commentContent.length() < 1 || commentContent.length() > 800) {
//            return CommonResult.failed("评论内容在1-800个字符之间！");
//        }
//
//        User user = (User) request.getSession().getAttribute("user");
//        if (Objects.isNull(user)) {
//            return CommonResult.failed("客官！您的登录过期，请从新登录哦");
//        }
//
//        String userId = user.getUserId();
//
//        CommentReply commentReply = new CommentReply();
//        commentReply.setCommentId(commentId);
//        commentReply.setReArticleId(articleId);
//        commentReply.setReplyUserId(userId);
//        commentReply.setReplyContent(commentContent);
//        commentReply.setRCommentTime(LocalDateTime.now());
//        commentReply.setCommentGoodNumber(0);
//        if (commentReplyService.save(commentReply)) {
//            CommentReplyVo commentReplyVo = new CommentReplyVo();
//            BeanUtils.copyProperties(commentReply, commentReplyVo);
//            commentReplyVo.setUserName(userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUserId, commentReplyVo.getUserId()).select(User::getUserName)).getUserName());
//            commentReplyVo.setCommentTime(DateUtil.format(commentReply1.getCommentTime(),"yyyy-MM-dd HH:mm:ss"));
//            return CommonResult.success(commentReplyVo);
//        }
//        return CommonResult.failed("评论失败");
//
//    }
}
