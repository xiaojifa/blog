package com.hzy.blog.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzy.blog.dto.article.PublishArticleActionDto;
import com.hzy.blog.dto.topic.PublishTopicActionDto;
import com.hzy.blog.entity.*;
import com.hzy.blog.service.*;
import com.hzy.blog.utils.CommonPage;
import com.hzy.blog.utils.CommonResult;
import com.hzy.blog.vo.ArticleVo;
import com.hzy.blog.vo.CommentTVo;
import com.hzy.blog.vo.CommentVo;
import com.hzy.blog.vo.TopicVo;
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

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadFile")
    @ResponseBody
    public String uploadFile(HttpServletRequest request, MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }
        User user = (User) request.getSession().getAttribute("user");
        if (Objects.isNull(user.getUserPublishArticle()) || user.getUserPublishArticle() != 1) {
            return null;
        }

        return uploadFileListService.getUploadFileUrl(file);
    }


    /**
     * 用户管理页面
     *
     * @return
     */
    @GetMapping("/manager")
    public String userManager() {
        return "/user/userManager";
    }

    /**
     * 用户收藏话题
     *
     * @param pageNumber
     * @return
     */
    @GetMapping("/collectionTopic/list")
    public String collectionTopicList(HttpServletRequest request, Integer pageNumber, Model model) {
        User user = (User) request.getSession().getAttribute("user");
        Page<Topic> topicPage = new Page<>(pageNumber, 24);
        List<String> topicIdList = userCollectionTopicService.list(Wrappers.<UserCollectionTopic>lambdaQuery()
                        .eq(UserCollectionTopic::getUserId, user.getUserId())
                        .select(UserCollectionTopic::getTopicId)).stream()
                .map(UserCollectionTopic::getTopicId)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(topicIdList)) {
            LambdaQueryWrapper<Topic> wrapper = Wrappers.<Topic>lambdaQuery()
                    .in(Topic::getTopicId, topicIdList)
                    .select(Topic::getTopicId,
                            Topic::getTopicAddTime,
                            Topic::getTopicCollectionNumber,
                            Topic::getTopicGoodNumber,
                            Topic::getTopicLookNumber,
                            Topic::getTopicTitle);
            IPage<Topic> topicIPage = topicService.page(topicPage, wrapper);
            model.addAttribute("topicIPage", CommonPage.restPage(topicIPage));
        }
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
        User user = (User) request.getSession().getAttribute("user");
        Page<TopicVo> topicPage = new Page<>(pageNumber, 24);
        IPage<TopicVo> topicVoIPage = topicService.topicList(topicPage, null, null);
        model.addAttribute("topicVoIPage", CommonPage.restPage(topicVoIPage));
        return "/user/topicList";
    }

    /**
     * 用户收藏文章
     *
     * @param pageNumber
     * @return
     */
    @GetMapping("/collection/list")
    public String collectionList(HttpServletRequest request, Integer pageNumber, Model model) {
        User user = (User) request.getSession().getAttribute("user");

        Page<Article> articlePage = new Page<>(pageNumber, 24);


        List<String> articleIdList = userCollectionArticleService.list(Wrappers.<UserCollectionArticle>lambdaQuery()
                        .eq(UserCollectionArticle::getUserId, user.getUserId())
                        .select(UserCollectionArticle::getArticleId)).stream()
                .map(UserCollectionArticle::getArticleId)
                .collect(Collectors.toList());
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
        User user = (User) request.getSession().getAttribute("user");
        Page<ArticleVo> articlePage = new Page<>(pageNumber, 24);
        IPage<ArticleVo> articleVoIPage = articleService.articleList(articlePage, null, null);
        model.addAttribute("articleVoIPage", CommonPage.restPage(articleVoIPage));
        return "/user/articleList";
    }

    /**
     * 发布话题
     *
     * @return
     */
    @GetMapping("/publishTopic")
    public String releaseTopic(HttpServletRequest request, Model model, String topicId) {
        User user = (User) request.getSession().getAttribute("user");
        if (Objects.isNull(user) || Objects.isNull(user.getUserPublishTopic()) || user.getUserPublishTopic() != 1) {
            return "redirect:/";
        }

        Topic topic = topicService.getOne(Wrappers.<Topic>lambdaQuery().eq(Topic::getUserId, user.getUserId()).eq(Topic::getTopicId, topicId));
        if (Objects.nonNull(topic)) {
            model.addAttribute("topic", topic);

            //获取文章标签
            List<TopicTagList> topicTagLists = topicTagListService.list(Wrappers.<TopicTagList>lambdaQuery()
                    .eq(TopicTagList::getTopicId, topic.getTopicId())
                    .select(TopicTagList::getTopicTagId));

            if (CollUtil.isNotEmpty(topicTagLists)) {
                List<String> topicTagIdList = topicTagLists.stream().map(TopicTagList::getTopicTagId).collect(Collectors.toList());
                model.addAttribute("topicTagIdList", topicTagIdList);
            }
            //获取该文章类型的同级类型
            String topicTypeId = topic.getTopicTypeId();
            List<TopicType> topicSameTypeList = topicTypeService.list(Wrappers.<TopicType>lambdaQuery().eq(TopicType::getTopicTypeParentId, topicTypeService.getById(topicTypeId).getTopicTypeParentId()));
            model.addAttribute("topicSameTypeList", topicSameTypeList);

            //获取该文章上级类型
            model.addAttribute("topicTypeParentId", topicTypeService.getById(topic.getTopicTypeId()).getTopicTypeParentId());
        }

        //获取类型
        List<TopicType> topicType0List = topicTypeService.list(Wrappers.<TopicType>lambdaQuery().isNull(TopicType::getTopicTypeParentId).or().eq(TopicType::getTopicTypeParentId, "").orderByAsc(TopicType::getTopicTypeSort));
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
        if (StrUtil.isBlank(topicTypeId)) {
            return CommonResult.failed("请选择一级分类");
        }

        List<TopicType> topicTypeList = topicTypeService.list(Wrappers.<TopicType>lambdaQuery()
                .eq(TopicType::getTopicTypeParentId, topicTypeId)
                .select(TopicType::getTopicTypeId, TopicType::getTopicTypeName));

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
        User user = (User) session.getAttribute("user");
        if (Objects.isNull(user)){
            return CommonResult.failed("登录过期，请重新登录");
        }
        User serviceById = userService.getById(user.getUserId());
        if(Objects.isNull(serviceById)){
            session.setAttribute("user",serviceById);
        }
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


            publishTopicActionDto.setTopicCoverUrl(uploadFileListService.getUploadFileUrl(topicCoverFile));
        }
        return topicService.publishTopicAction(request, publishTopicActionDto);
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
            session.setAttribute("user",serviceById);
        }
        if (Objects.isNull(serviceById.getUserPublishArticle()) || serviceById.getUserPublishArticle() != 1) {
            return CommonResult.failed("当前您还没有权限发布，请联系管理员");
        }
        if (Objects.nonNull(articleCoverFile)) {
//            //判断是否上传的图片，是否是我们指定的像素
//            BufferedImage read = ImageIO.read(articleCoverFile.getInputStream());
//            if (Objects.isNull(read)) {
//                return CommonResult.failed("请上传图片文件");
//            }
//            int width = read.getWidth();
//            int height = read.getHeight();
//            if (width != 250 || height != 170) {
//                return CommonResult.failed("图片的像素为 250px * 170px");
//            }

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
        User user = (User) request.getSession().getAttribute("user");
        if (Objects.isNull(pageNumber) || pageNumber < 1) {
            pageNumber = 1;
        }
        Page<TopicVo> topicPage = new Page<>(pageNumber, 24);
        IPage<TopicVo> topicVoIPage = topicService.topicList(topicPage, null, user.getUserId());
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
        if (StrUtil.isBlank(topicId) || StrUtil.isBlank(commentTContent)) {
            return CommonResult.failed("评论失败，请刷新页面重试");
        }
        if (commentTContent.isEmpty() || commentTContent.length() > 800) {
            return CommonResult.failed("评论内容在1-800个字符之间！");
        }

        User user = (User) request.getSession().getAttribute("user");
        if (Objects.isNull(user)) {
            return CommonResult.failed("客官！您的登录过期，请从新登录哦");
        }
        String userId = user.getUserId();

        CommentT commentT1 = new CommentT();
        commentT1.setTopicId(topicId);
        commentT1.setUserId(userId);
        commentT1.setCommentTContent(commentTContent);
        commentT1.setCommentTTime(LocalDateTime.now());
        commentT1.setCommentTGoodNumber(0);

        if (commentTService.save(commentT1)) {
            CommentTVo commentTVo = new CommentTVo();
            BeanUtils.copyProperties(commentT1, commentTVo);
            commentTVo.setUserName(
                    userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUserId, commentTVo.getUserId()).select(User::getUserName)).getUserName()
            );
            commentTVo.setCommentTTime(DateUtil.format(commentT1.getCommentTTime(),"yyyy-MM-dd HH:mm:ss"));
            return CommonResult.success(commentTVo);
        }
        return CommonResult.failed("评论失败");
    }

    /**
     * 用户评论
     *
     * @param request
     * @param articleId
     * @param commentContent
     * @return
     */
    @PostMapping("/saveComment")
    @ResponseBody
    public CommonResult userSaveComment(HttpServletRequest request, String articleId, String commentContent,String commentId) {
        if (StrUtil.isBlank(articleId) || StrUtil.isBlank(commentContent)) {
            return CommonResult.failed("评论失败，请刷新页面重试");
        }
        if (commentContent.isEmpty() || commentContent.length() > 800) {
            return CommonResult.failed("评论内容在1-800个字符之间！");
        }

        User user = (User) request.getSession().getAttribute("user");
        if (Objects.isNull(user)) {
            return CommonResult.failed("客官！您的登录过期，请从新登录哦");
        }
        String userId = user.getUserId();


//        Comment comment = commentService.getOne(Wrappers.<Comment>lambdaQuery().eq(Comment::getUserId, userId).select(Comment::getCommentTime).orderByDesc(Comment::getCommentTime), false);
//        if (Objects.nonNull(comment) && Objects.nonNull(comment.getCommentTime())) {
//            if ((comment.getCommentTime().plusSeconds(10000)) > System.currentTimeMillis()) {
//                return CommonResult.failed("客官您评论太快啦~~，休息一下吧");
//            }
//        }

        Comment comment1 = new Comment();
        comment1.setArticleId(articleId);
        comment1.setUserId(userId);
        comment1.setCommentContent(commentContent);
        comment1.setCommentTime(LocalDateTime.now());
        comment1.setCommentGoodNumber(0);

        if (commentService.save(comment1)) {
            CommentVo commentVo = new CommentVo();
            BeanUtils.copyProperties(comment1, commentVo);
            commentVo.setUserName(
                    userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUserId, commentVo.getUserId()).select(User::getUserName)).getUserName()
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
//
//        User user = (User) request.getSession().getAttribute("user");
//        if (Objects.isNull(user)) {
//            return CommonResult.failed("客官！您的登录过期，请从新登录哦");
//        }
//        String userId = user.getUserId();
//
//
//        CommentReply commentReply = ICommentReplyService.getOne(Wrappers.<CommentReply>lambdaQuery().eq(CommentReply::getUserId, userId).select(Comment::getCommentTime).orderByDesc(Comment::getCommentTime), false);
//        if (Objects.nonNull(comment) && Objects.nonNull(comment.getCommentTime())) {
//            if ((comment.getCommentTime().getTime() + 10000) > System.currentTimeMillis()) {
//                return CommonResult.failed("客官您评论太快啦~~，休息一下吧");
//            }
//        }
//    }
}
