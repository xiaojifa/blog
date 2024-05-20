package com.hzy.blog.controller;

import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.hzy.blog.vo.*;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzy.blog.dto.user.UserInfoDto;
import com.hzy.blog.entity.*;
import com.hzy.blog.service.*;
import com.hzy.blog.utils.CommonPage;
import com.hzy.blog.utils.CommonResult;
import com.hzy.blog.utils.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/27 19:20
 */
@Controller
public class ViewController {

    @Resource
    private IUserService userService;
    @Resource
    private IArticleService articleService;
    @Resource
    private IArticleTypeService articleTypeService;
    @Resource
    private IArticleTagService articleTagService;
    @Resource
    private IArticleTagListService articleTagListService;
    @Resource
    private ICommentService commentService;
    @Resource
    private ICommentReplyService commentReplyService;
    @Resource
    private ICommentTService commentTService;
    @Resource
    private ServletContext servletContext;
    @Resource
    private IAdTypeService adTypeService;
    @Resource
    private IAdService adService;
    @Resource
    private ILinkService linkService;
    @Resource
    private ITopicService topicService;
    @Resource
    private ITopicTagService topicTagService;
    @Resource
    private ITopicTypeService topicTypeService;
    @Resource
    private ITopicTagListService topicTagListService;

    /**
     * 清除首页缓存
     *
     * @return
     */
    @GetMapping("/ci")
    public String clearCache() {
        servletContext.removeAttribute("articleTypeList");
        servletContext.removeAttribute("articleHotList");
        servletContext.removeAttribute("articleTagList");
        servletContext.removeAttribute("adIndexList");
        servletContext.removeAttribute("linkList");
        return "redirect:/";
    }

    /**
     * 聊天
     *
     * @param message
     * @return
     */
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        return message;
    }

    /**
     * 获取图像验证码
     *
     * @throws IOException
     */
    @GetMapping("/getCaptcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CircleCaptcha captcha = CommonUtils.getCaptcha(request);
        captcha.write(response.getOutputStream());
    }

    /**
     * 用户注册页面
     *
     * @param request
     * @return
     */
    @GetMapping("/register")
    public String register(HttpServletRequest request) {
        if (Objects.nonNull(request.getSession().getAttribute("user"))) {
            return "redirect:/";
        }

        return "/view/register";
    }

    /**
     * 用户注册方法
     *
     * @param request
     * @param userInfoDto
     * @return
     */
    @PostMapping("/userRegister")
    @ResponseBody
    public CommonResult userRegister(HttpServletRequest request, UserInfoDto userInfoDto) {
        HttpSession session = request.getSession();
        String verifyCode = userInfoDto.getVerifyCode();
        if (StrUtil.isBlank(verifyCode) || !verifyCode.equals(session.getAttribute("circleCaptchaCode"))) {
            session.removeAttribute("circleCaptchaCode");
            return CommonResult.failed("验证码不正确");
        }
        //用户名和密码是否相同
        if (userInfoDto.getUserName().equals(userInfoDto.getUserPassword())) {
            session.removeAttribute("circleCaptchaCode");
            return CommonResult.failed("用户名和密码不能相同哦");
        }

        //用户名是否已经被使用
        if (userService.count(Wrappers.<User>lambdaQuery().eq(User::getUserName, userInfoDto.getUserName())) > 0) {
            session.removeAttribute("circleCaptchaCode");
            return CommonResult.failed("该用户名已经存在哦");
        }

        User user = new User();
        BeanUtils.copyProperties(userInfoDto, user);
        user.setUserId(IdUtil.simpleUUID());
        user.setUserRegisterTime(LocalDateTime.now());
        user.setUserPassword(SecureUtil.md5(user.getUserId() + user.getUserPassword()));
        user.setUserFrozen(0);
        user.setUserPublishArticle(1);
        user.setUserVip(0);
        if (userService.save(user)) {
            return CommonResult.success("注册成功");
        }

        return CommonResult.failed("哎呀！注册失败啦，刷新页面再试一次哦~");
    }


    /**
     * 用户登陆页面
     *
     * @param request
     * @return
     */
    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        if (Objects.nonNull(request.getSession().getAttribute("user"))) {
            return "redirect:/";
        }
        model.addAttribute("referer", request.getHeader("referer"));
        return "/view/login";
    }

    /**
     * 用户登录方法
     *
     * @param request
     * @param userInfoDto
     * @return
     */
    @PostMapping("/userLogin")
    @ResponseBody
    public CommonResult userLogin(HttpServletRequest request, UserInfoDto userInfoDto) {
        HttpSession session = request.getSession();
        String verifyCode = userInfoDto.getVerifyCode();
        session.setAttribute("userName", userInfoDto.getUserName());
        if (StrUtil.isBlank(verifyCode) || !verifyCode.equals(session.getAttribute("circleCaptchaCode"))) {
            session.removeAttribute("circleCaptchaCode");
            return CommonResult.failed("验证码不正确");
        }
        //用户名和密码是否相同
        if (userInfoDto.getUserName().equals(userInfoDto.getUserPassword())) {
            session.removeAttribute("circleCaptchaCode");
            return CommonResult.failed("用户名和密码不能相同哦");
        }

        //获取用户
        User userDb = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, userInfoDto.getUserName()), false);
        if (Objects.isNull(userDb)) {
            session.removeAttribute("circleCaptchaCode");
            return CommonResult.failed("用户名错误");
        }
        if (Objects.nonNull(userDb.getUserFrozen()) && userDb.getUserFrozen() == 1) {
            session.removeAttribute("circleCaptchaCode");
            return CommonResult.failed("您的账户已经被冻结，暂无法登录，请联系管理员");
        }

        if (!SecureUtil.md5(userDb.getUserId() + userInfoDto.getUserPassword()).equals(userDb.getUserPassword())) {
            session.removeAttribute("circleCaptchaCode");
            return CommonResult.failed("密码错误");
        }
        session.setAttribute("user", userDb);
        return CommonResult.success("登录成功");
    }

    /**
     * 用户退出登录
     *
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return "/";
    }

    /**
     * 首页
     *
     * @return
     */
    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {

        ServletContext servletContext = request.getServletContext();

        // 查询获取首页文章类型树形目录
        List<ArticleTypeTreeVo> articleTypeList = (List<ArticleTypeTreeVo>) servletContext.getAttribute("articleTypeList");
        if (CollUtil.isEmpty(articleTypeList)) {
            articleTypeList = articleTypeService.getIndexArticleTypeList(null);
            if (CollUtil.isNotEmpty(articleTypeList)) {
                for (ArticleTypeTreeVo articleTypeTreeVo : articleTypeList) {
                    articleTypeTreeVo.setArticleTypeTreeVoList(articleTypeService.getIndexArticleTypeList(articleTypeTreeVo.getArticleTypeId()));
                }
            }
            servletContext.setAttribute("articleTypeList", articleTypeList);
        }

        // 热门话题
        List<Topic> topicHotList = (List<Topic>) servletContext.getAttribute("topicHotList");
        if (CollUtil.isEmpty(topicHotList)) {
            topicHotList = topicService.list(Wrappers.<Topic>lambdaQuery()
                    .eq(Topic::getTopicHot, 1)
                    .select(Topic::getTopicId, Topic::getTopicTitle)
                    .last(" limit 5"));
            servletContext.setAttribute("topicHotList", topicHotList);
        }

        // 热门文章
        List<Article> articleHotList = (List<Article>) servletContext.getAttribute("articleHotList");
        if (CollUtil.isEmpty(articleHotList)) {
            articleHotList = articleService.list(Wrappers.<Article>lambdaQuery()
                    .eq(Article::getArticleHot, 1)
                    .select(Article::getArticleId, Article::getArticleTitle)
                    .last(" limit 5"));
            servletContext.setAttribute("articleHotList", articleHotList);
        }

        // 热门话题标签
        List<TopicTag> topicTagList = (List<TopicTag>) servletContext.getAttribute("topicTagList");
        if (CollUtil.isEmpty(topicTagList)) {
            topicTagList = topicTagService.list(Wrappers.<TopicTag>lambdaQuery()
                    .select(TopicTag::getTopicTagId, TopicTag::getTopicTagName));
            servletContext.setAttribute("topicTagList", topicTagList);
        }

        // 热门文章标签
        List<ArticleTag> articleTagList = (List<ArticleTag>) servletContext.getAttribute("articleTagList");
        if (CollUtil.isEmpty(articleTagList)) {
            articleTagList = articleTagService.list(Wrappers.<ArticleTag>lambdaQuery()
                    .select(ArticleTag::getArticleTagId, ArticleTag::getArticleTagName));
            servletContext.setAttribute("articleTagList", articleTagList);
        }

        // 广告
        List<Ad> adIndexList = (List<Ad>) servletContext.getAttribute("adIndexList");
        if (CollUtil.isEmpty(adIndexList)) {
            AdType homeAd = adTypeService.getOne(Wrappers.<AdType>lambdaQuery()
                    .eq(AdType::getAdTypeTag, "homeAd")
                    .select(AdType::getAdTypeId));
            if (Objects.nonNull(homeAd)) {
                DateTime date = DateUtil.date();
                adIndexList = adService.list(Wrappers.<Ad>lambdaQuery()
                        .eq(Ad::getAdTypeId, homeAd.getAdTypeId())
                        .lt(Ad::getAdBeginTime, date)
                        .gt(Ad::getAdEndTime, date)
                        .select(Ad::getAdId, Ad::getAdImgUrl, Ad::getAdLinkUrl, Ad::getAdTitle)
                        .orderByAsc(Ad::getAdSort));
                System.out.println(adIndexList);
                servletContext.setAttribute("adIndexList", adIndexList);
            }
        }

        // 首页最新文章
        List<ArticleVo> indexArticleList = (List<ArticleVo>) servletContext.getAttribute("indexArticleList");
        if (CollUtil.isEmpty(indexArticleList)) {
            indexArticleList = articleService.getIndexArticleList();
            servletContext.setAttribute("indexArticleList", indexArticleList);
        }

        // 首页最新话题
        List<TopicVo> indexTopicList = (List<TopicVo>) servletContext.getAttribute("indexTopicList");
        if (CollUtil.isEmpty(indexTopicList)) {
            indexTopicList = topicService.getIndexTopicList();
            servletContext.setAttribute("indexTopicList", indexTopicList);
        }

        // 友情连接
        List<Link> linkList = (List<Link>) servletContext.getAttribute("linkList");
        if (CollUtil.isEmpty(linkList)) {
            linkList = linkService.list(Wrappers.<Link>lambdaQuery().orderByAsc(Link::getLinkSort));
            servletContext.setAttribute("linkList", linkList);
        }

        return "/view/index";

    }

    /**
     * 话题列表
     *
     * @param pageNumber
     * @return
     */
    @GetMapping("/topic/list")
    public String topicListView(Integer pageNumber, String topicTitle, String topicTypeId, Model model) {
        Page<TopicVo> topicPage = new Page<>(Objects.isNull(pageNumber) ? 1 : pageNumber, 24);
        IPage<TopicVo> topicVoIPage = topicService.topicListView(topicPage, topicTitle, topicTypeId);

        //文章列表
        model.addAttribute("topicVoIPage", CommonPage.restPage(topicVoIPage));

        //文章分类名称
        if (StrUtil.isNotBlank(topicTypeId)) {
            TopicType topicType = topicTypeService.getOne(Wrappers.<TopicType>lambdaQuery().eq(TopicType::getTopicTypeId, topicTypeId).select(TopicType::getTopicTypeName), false);
            model.addAttribute("topicTypeName", topicType.getTopicTypeName());
            model.addAttribute("topicTypeId", topicTypeId);
        }

        return "/view/topicList";
    }

    /**
     * 文章列表
     *
     * @param pageNumber
     * @return
     */
    @GetMapping("/article/list")
    public String articleListView(Integer pageNumber, String articleTitle, String articleTypeId, Model model) {
        Page<ArticleVo> articlePage = new Page<>(Objects.isNull(pageNumber) ? 1 : pageNumber, 24);
        IPage<ArticleVo> articleVoIPage = articleService.articleListView(articlePage, articleTitle, articleTypeId);

        //文章列表
        model.addAttribute("articleVoIPage", CommonPage.restPage(articleVoIPage));

        //文章分类名称
        if (StrUtil.isNotBlank(articleTypeId)) {
            ArticleType articleType = articleTypeService.getOne(Wrappers.<ArticleType>lambdaQuery().eq(ArticleType::getArticleTypeId, articleTypeId).select(ArticleType::getArticleTypeName), false);
            model.addAttribute("articleTypeName", articleType.getArticleTypeName());
            model.addAttribute("articleTypeId", articleTypeId);
        }

        return "/view/articleList";
    }

    /**
     * 获取标签对应的话题列表
     *
     * @param topicTagId
     * @param pageNumber
     * @return
     */
    @GetMapping("/tag/topic/list")
    public String tagTopicList(String topicTagId, Integer pageNumber, Model model) {
        if (StrUtil.isBlank(topicTagId)) {
            return "redirect:/";
        }
        if (Objects.isNull(pageNumber) || pageNumber < 1) {
            pageNumber = 1;
        }
        Page<TopicVo> topicPage = new Page<>(Objects.isNull(pageNumber) ? 1 : pageNumber, 24);
        IPage<TopicVo> topicVoIPage = topicService.tagTopicList(topicPage, topicTagId);
        model.addAttribute("topicVoIPage", CommonPage.restPage(topicVoIPage));

        //获取标签类型
        TopicTag topicTag = topicTagService.getOne(Wrappers.<TopicTag>lambdaQuery().eq(TopicTag::getTopicTagId, topicTagId));
        if (Objects.nonNull(topicTag)) {
            model.addAttribute("topicTagName", topicTag.getTopicTagName());
        }

        model.addAttribute("topicTagId", topicTagId);
        return "/view/tagTopicList";
    }

    /**
     * 获取标签对应的文章列表
     *
     * @param articleTagId
     * @param pageNumber
     * @return
     */
    @GetMapping("/tag/article/list")
    public String tagArticleList(String articleTagId, Integer pageNumber, Model model) {
        if (StrUtil.isBlank(articleTagId)) {
            return "redirect:/";
        }
        if (Objects.isNull(pageNumber) || pageNumber < 1) {
            pageNumber = 1;
        }
        Page<ArticleVo> articlePage = new Page<>(Objects.isNull(pageNumber) ? 1 : pageNumber, 24);
        IPage<ArticleVo> articleVoIPage = articleService.tagArticleList(articlePage, articleTagId);
        model.addAttribute("articleVoIPage", CommonPage.restPage(articleVoIPage));

        //获取标签类型
        ArticleTag articleTag = articleTagService.getOne(Wrappers.<ArticleTag>lambdaQuery().eq(ArticleTag::getArticleTagId, articleTagId));
        if (Objects.nonNull(articleTag)) {
            model.addAttribute("articleTagName", articleTag.getArticleTagName());
        }

        model.addAttribute("articleTagId", articleTagId);
        return "/view/tagArticleList";

    }

    /**
     * 话题
     *
     * @param topicId
     * @return
     */
    @GetMapping("/topic")
    public String topicView(HttpServletRequest request, String topicId, Model model) {
        HttpSession session = request.getSession();

        TopicVo topicVo = topicService.getTopic(topicId);
        if (Objects.isNull(topicVo)) {
            return "redirect:/";
        }

        Topic topic = topicService.getOne(Wrappers.<Topic>lambdaQuery().eq(Topic::getTopicId, topicVo.getTopicId()).select(Topic::getTopicId, Topic::getTopicLookNumber), false);
        //添加查看次数
        Integer topicLookNumber = topic.getTopicLookNumber();
        if (Objects.isNull(topicLookNumber) || topicLookNumber < 0) {
            topicLookNumber = 0;
        }
        ++topicLookNumber;
        topic.setTopicLookNumber(topicLookNumber);
        topicService.updateById(topic);


        //隐藏作者用户名
        String userName = topicVo.getUserName();
        if (StrUtil.isNotBlank(userName)) {
            topicVo.setUserName(CommonUtils.getHideMiddleStr(userName));
        }

        //话题
        model.addAttribute("topic", topicVo);

        //话题类型
        if (Objects.nonNull(topicVo) && StrUtil.isNotBlank(topicVo.getTopicTypeId())) {
            TopicType topicType = topicTypeService.getOne(Wrappers.<TopicType>lambdaQuery().eq(TopicType::getTopicTypeId, topicVo.getTopicTypeId()).select(TopicType::getTopicTypeName, TopicType::getTopicTypeId), false);
            model.addAttribute("topicType", topicType);
        }

        return "/view/topic";
    }

    /**
     * 文章
     *
     * @param articleId
     * @return
     */
    @GetMapping("/article")
    public String articleView(HttpServletRequest request, String articleId,Model model) {
        HttpSession session = request.getSession();
      User user= (User) session.getAttribute("user");

       Integer userVip=user.getUserVip();
        ArticleVo articleVo = articleService.getArticle(articleId);
        if (Objects.isNull(articleVo)) {
            return "redirect:/";
        }
        Article article = articleService.getOne(Wrappers.<Article>lambdaQuery().eq(Article::getArticleId, articleVo.getArticleId()).select(Article::getArticleId, Article::getArticleLookNumber), false);

        //添加查看次数
        Integer articleLookNumber = article.getArticleLookNumber();
        if (Objects.isNull(articleLookNumber) || articleLookNumber < 0) {
            articleLookNumber = 0;
        }
        ++articleLookNumber;
        article.setArticleLookNumber(articleLookNumber);
        articleService.updateById(article);

        //隐藏作者用户名
        String userName = articleVo.getUserName();
        if (StrUtil.isNotBlank(userName)) {
            articleVo.setUserName(CommonUtils.getHideMiddleStr(userName));
        }

        //文章
        model.addAttribute("article", articleVo);

        //文章类型
        if (Objects.nonNull(articleVo) && StrUtil.isNotBlank(articleVo.getArticleTypeId())) {
            ArticleType articleType = articleTypeService.getOne(Wrappers.<ArticleType>lambdaQuery().eq(ArticleType::getArticleTypeId, articleVo.getArticleTypeId()).select(ArticleType::getArticleTypeName, ArticleType::getArticleTypeId), false);
            model.addAttribute("articleType", articleType);
        }

        if(userVip==1){
    return "/view/article";
} else if (userVip == 0 && articleVo.getArticleVip().equals(userVip)) {
    return "/view/article";
}else {
            model.addAttribute("message", "Vip文章，您还不是Vip");

}
        return "redirect:/";
    }


    /**
     * 获取话题评论列表
     *
     * @param topicId
     * @param pageNumber
     * @return
     */
    @PostMapping("/commentT/list")
    @ResponseBody
    public CommonResult commentTList(HttpServletRequest request, String topicId, Integer pageNumber) {
        if (StrUtil.isBlank(topicId)) {
            return CommonResult.failed("程序出现错误，请刷新页面重试");
        }
        if (Objects.isNull(pageNumber) || pageNumber < 1) {
            pageNumber = 1;
        }
        Page<CommentTVo> commentTVoPage = new Page<>(pageNumber, 5);
        IPage<CommentTVo> commentTVoIPage = commentTService.getTopicCommentTList(commentTVoPage, topicId);
        commentTVoIPage.getRecords().stream().forEach(commentTVo -> {
            commentTVo.setUserName(CommonUtils.getHideMiddleStr(commentTVo.getUserName()));
        });

        //已经点过赞的评论
        HashMap<String, Long> goodCommentTMap = (HashMap<String, Long>) request.getSession().getAttribute("goodCommentTMap");
        if (CollUtil.isNotEmpty(goodCommentTMap)) {
            List<String> commentTIds = goodCommentTMap.keySet().stream().collect(Collectors.toList());
            commentTVoIPage.getRecords().stream().forEach(commentTVo -> {
                if (commentTIds.contains(commentTVo.getCommentTId())) {
                    commentTVo.setIsGoodCommentT(1);
                }
            });
        }

        return CommonResult.success(CommonPage.restPage(commentTVoIPage));
    }

    /**
     * 获取文章评论列表
     *
     * @param articleId
     * @param pageNumber
     * @return
     */
    @PostMapping("/comment/list")
    @ResponseBody
    public CommonResult commentList(HttpServletRequest request, String articleId, Integer pageNumber) {
        if (StrUtil.isBlank(articleId)) {
            return CommonResult.failed("程序出现错误，请刷新页面重试");
        }
        if (Objects.isNull(pageNumber) || pageNumber < 1) {
            pageNumber = 1;
        }
        Page<CommentVo> commentVoPage = new Page<>(pageNumber, 5);
        IPage<CommentVo> commentVoIPage = commentService.getArticleCommentList(commentVoPage, articleId);
        commentVoIPage.getRecords().stream().forEach(commentVo -> {
            commentVo.setUserName(CommonUtils.getHideMiddleStr(commentVo.getUserName()));
        });

        //已经点过赞的评论
        HashMap<String, Long> goodCommentMap = (HashMap<String, Long>) request.getSession().getAttribute("goodCommentMap");
        if (CollUtil.isNotEmpty(goodCommentMap)) {
            List<String> commentIds = goodCommentMap.keySet().stream().collect(Collectors.toList());
            commentVoIPage.getRecords().stream().forEach(commentVo -> {
                if (commentIds.contains(commentVo.getCommentId())) {
                    commentVo.setIsGoodComment(1);
                }
            });
        }

        return CommonResult.success(CommonPage.restPage(commentVoIPage));
    }

    /**
     * 联系
     *
     * @return
     */
    @GetMapping("/contact")
    public String contact() {
        return "/view/contact";
    }

    /**
     * 捐赠
     *
     * @return
     */
    @GetMapping("/donation")
    public String donation(Model model) {
        return "/view/donation";
    }

    /**
     * 话题点赞
     *
     * @param request
     * @param topicId
     * @return
     */
    @PostMapping("/topicGood")
    @ResponseBody
    public CommonResult topicGood(HttpServletRequest request, String topicId) {
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("topicGoodTime"))) {
            return CommonResult.failed("客官！您已经点过啦");
        }

        Topic topic = topicService.getById(topicId);
        Integer topicGoodNumber = topic.getTopicGoodNumber();
        ++topicGoodNumber;
        topic.setTopicGoodNumber(topicGoodNumber);
        if (topicService.updateById(topic)) {
            session.setAttribute("topicGoodTime", true);
            return CommonResult.success("点赞成功！");
        }

        return CommonResult.failed("点赞失败");
    }

    /**
     * 文章点赞
     *
     * @param request
     * @param articleId
     * @return
     */
    @PostMapping("/articleGood")
    @ResponseBody
    public CommonResult articleGood(HttpServletRequest request, String articleId) {
        HttpSession session = request.getSession();
        if (Objects.nonNull(session.getAttribute("articleGoodTime"))) {
            return CommonResult.failed("客官！您已经点过啦");
        }

        Article article = articleService.getById(articleId);
        Integer articleGoodNumber = article.getArticleGoodNumber();
        ++articleGoodNumber;
        article.setArticleGoodNumber(articleGoodNumber);
        if (articleService.updateById(article)) {
            session.setAttribute("articleGoodTime", true);
            return CommonResult.success("点赞成功！");
        }

        return CommonResult.failed("点赞失败");
    }

    /**
     * 收藏话题
     *
     * @param request
     * @param topicId
     * @return
     */
    @PostMapping("/topicCollection")
    @ResponseBody
    public CommonResult topicCollection(HttpServletRequest request, String topicId) {
        User user = (User) request.getSession().getAttribute("user");
        if (Objects.isNull(user)) {
            return CommonResult.failed("客官！您还没有登录呢");
        }
        return topicService.topicCollection(user, topicId);
    }

    /**
     * 收藏文章
     *
     * @param request
     * @param articleId
     * @return
     */
    @PostMapping("/articleCollection")
    @ResponseBody
    public CommonResult articleCollection(HttpServletRequest request, String articleId) {
        User user = (User) request.getSession().getAttribute("user");
        if (Objects.isNull(user)) {
            return CommonResult.failed("客官！您还没有登录呢");
        }
        return articleService.articleCollection(user, articleId);
    }

    /**
     * 搜索话题
     *
     * @param request
     * @param topicTitle
     * @return
     */
    @GetMapping("/topic/search")
    public String topicSearch(HttpServletRequest request, Integer pageNumber, String topicTitle, Model model) {
        if (StrUtil.isBlank(topicTitle)) {
            return "/";
        }
        topicTitle = topicTitle.trim();
        model.addAttribute("topicTitle", topicTitle);
        if (Objects.isNull(pageNumber) || pageNumber < 1) {
            pageNumber = 1;
        }
        String ipAddr = CommonUtils.getIpAddr(request);
        ServletContext servletContext = request.getServletContext();
        ConcurrentMap<String, Long> topicSearchMap = (ConcurrentMap<String, Long>) servletContext.getAttribute("topicSearchMap");
        if (CollUtil.isEmpty(topicSearchMap) || Objects.isNull(topicSearchMap.get(ipAddr))) {
            topicSearchMap = new ConcurrentHashMap<>();
            topicSearchMap.put(ipAddr, DateUtil.currentSeconds());
        } else {
            if ((topicSearchMap.get(ipAddr) + 1 > DateUtil.currentSeconds())) {
                return "/view/searchErrors";
            }
        }
        //查询到的文章列表
        List<Topic> topicList = new ArrayList<>();

        //拆分搜索词,查询标签
        List<Word> words = WordSegmenter.seg(topicTitle);
        List<String> titleList = words.stream().map(Word::getText).collect(Collectors.toList());
        titleList.add(topicTitle);
        List<String> topicTagIdList = topicTagService.list(Wrappers.<TopicTag>lambdaQuery()
                .in(TopicTag::getTopicTagName, titleList)
                .select(TopicTag::getTopicTagId)).stream().map(TopicTag::getTopicTagId).collect(Collectors.toList());
        List<String> topicIdList = new ArrayList<>();
        if (CollUtil.isNotEmpty(topicTagIdList)) {
            topicIdList = topicTagListService.list(Wrappers.<TopicTagList>lambdaQuery()
                            .in(TopicTagList::getTopicTagId, topicTagIdList)
                            .select(TopicTagList::getTopicId)).stream()
                    .map(TopicTagList::getTopicId).collect(Collectors.toList());

        }

        //分页查询
        IPage<Topic> topicPage = new Page<>(pageNumber, 12);
        LambdaQueryWrapper<Topic> queryWrapper = Wrappers.<Topic>lambdaQuery()
                .like(Topic::getTopicTitle, topicTitle)
                .select(Topic::getTopicId,
                        Topic::getTopicCollectionNumber,
                        Topic::getTopicLookNumber,
                        Topic::getTopicAddTime,
                        Topic::getTopicTitle);
        if (CollUtil.isNotEmpty(topicIdList)) {
            queryWrapper.or().in(Topic::getTopicId, topicIdList);
        }

        IPage<Topic> topicIPage = topicService.page(topicPage, queryWrapper);
        model.addAttribute("topicIPage", CommonPage.restPage(topicIPage));

        //保持搜索时间
        topicSearchMap.put(ipAddr, DateUtil.currentSeconds());
        servletContext.setAttribute("topicSearchMap", topicSearchMap);

        return "/view/topicSearch";
    }

    /**
     * 搜索文章
     *
     * @param request
     * @param articleTitle
     * @return
     */
    @GetMapping("/article/search")
    public String articleSearch(HttpServletRequest request, Integer pageNumber, String articleTitle, Model model) {
        if (StrUtil.isBlank(articleTitle)) {
            return "/";
        }
        articleTitle = articleTitle.trim();
        model.addAttribute("articleTitle", articleTitle);
        if (Objects.isNull(pageNumber) || pageNumber < 1) {
            pageNumber = 1;
        }
        String ipAddr = CommonUtils.getIpAddr(request);
        ServletContext servletContext = request.getServletContext();
        ConcurrentMap<String, Long> articleSearchMap = (ConcurrentMap<String, Long>) servletContext.getAttribute("articleSearchMap");
        if (CollUtil.isEmpty(articleSearchMap) || Objects.isNull(articleSearchMap.get(ipAddr))) {
            articleSearchMap = new ConcurrentHashMap<>();
            articleSearchMap.put(ipAddr, DateUtil.currentSeconds());
        } else {
            if ((articleSearchMap.get(ipAddr) + 1 > DateUtil.currentSeconds())) {
                return "/view/searchError";
            }
        }
        //查询到的文章列表
        List<Article> articleList = new ArrayList<>();

        //拆分搜索词,查询标签
        List<Word> words = WordSegmenter.seg(articleTitle);
        List<String> titleList = words.stream().map(Word::getText).collect(Collectors.toList());
        titleList.add(articleTitle);
        List<String> articleTagIdList = articleTagService.list(Wrappers.<ArticleTag>lambdaQuery()
                .in(ArticleTag::getArticleTagName, titleList)
                .select(ArticleTag::getArticleTagId)).stream().map(ArticleTag::getArticleTagId).collect(Collectors.toList());
        List<String> articleIdList = new ArrayList<>();
        if (CollUtil.isNotEmpty(articleTagIdList)) {
            articleIdList = articleTagListService.list(Wrappers.<ArticleTagList>lambdaQuery()
                            .in(ArticleTagList::getArticleTagId, articleTagIdList)
                            .select(ArticleTagList::getArticleId)).stream()
                    .map(ArticleTagList::getArticleId).collect(Collectors.toList());

        }

        //分页查询
        IPage<Article> articlePage = new Page<>(pageNumber, 12);
        LambdaQueryWrapper<Article> queryWrapper = Wrappers.<Article>lambdaQuery()
                .like(Article::getArticleTitle, articleTitle)
                .select(Article::getArticleId,
                        Article::getArticleCoverUrl,
                        Article::getArticleCollectionNumber,
                        Article::getArticleLookNumber,
                        Article::getArticleAddTime,
                        Article::getArticleTitle);
        if (CollUtil.isNotEmpty(articleIdList)) {
            queryWrapper.or().in(Article::getArticleId, articleIdList);
        }

        IPage<Article> articleIPage = articleService.page(articlePage, queryWrapper);
        model.addAttribute("articleIPage", CommonPage.restPage(articleIPage));

        //保持搜索时间
        articleSearchMap.put(ipAddr, DateUtil.currentSeconds());
        servletContext.setAttribute("articleSearchMap", articleSearchMap);

        return "/view/articleSearch";
    }

    /**
     * 给话题点赞
     *
     * @param commentTId
     * @return
     */
    @PostMapping("/goodCommentT")
    @ResponseBody
    public CommonResult goodCommentT(HttpServletRequest request, String commentTId) {
        HttpSession session = request.getSession();


        if (StrUtil.isBlank(commentTId)) {
            return CommonResult.failed("未获取到需要的数据，请刷新页面重试");
        }

        //一个小时只能给一个评论点赞
        HashMap<String, Long> goodCommentTMap = (HashMap<String, Long>) session.getAttribute("goodCommentTMap");
        if (CollUtil.isEmpty(goodCommentTMap)) {
            goodCommentTMap = new HashMap<>();
        } else {
            if (Objects.nonNull(goodCommentTMap.get(commentTId))) {
                Long goodCommentTTime = goodCommentTMap.get(commentTId);
                if ((goodCommentTTime + 3600) >= DateUtil.currentSeconds()) {
                    return CommonResult.failed("客官，这个评论您已经点过赞了哦");
                }
            }
        }

        CommentT commentT = commentTService.getById(commentTId);
        if (Objects.isNull(commentT)) {
            return CommonResult.failed("点赞失败，未获取到需要的数据，请刷新页面重试");
        }
        Integer commentTGoodNumber = commentT.getCommentTGoodNumber();
        ++commentTGoodNumber;
        if (commentTService.updateById(commentT.setCommentTGoodNumber(commentTGoodNumber))) {
            goodCommentTMap.put(commentTId, DateUtil.currentSeconds());
            session.setAttribute("goodCommentTMap", goodCommentTMap);
            return CommonResult.success("点赞成功");
        }
        return CommonResult.failed("点赞失败");
    }

    /**
     * 给评论点赞
     *
     * @param commentId
     * @return
     */
    @PostMapping("/goodComment")
    @ResponseBody
    public CommonResult goodComment(HttpServletRequest request, String commentId) {
        HttpSession session = request.getSession();


        if (StrUtil.isBlank(commentId)) {
            return CommonResult.failed("未获取到需要的数据，请刷新页面重试");
        }

        //一个小时只能给一个评论点赞
        HashMap<String, Long> goodCommentMap = (HashMap<String, Long>) session.getAttribute("goodCommentMap");
        if (CollUtil.isEmpty(goodCommentMap)) {
            goodCommentMap = new HashMap<>();
        } else {
            if (Objects.nonNull(goodCommentMap.get(commentId))) {
                Long goodCommentTime = goodCommentMap.get(commentId);
                if ((goodCommentTime + 3600) >= DateUtil.currentSeconds()) {
                    return CommonResult.failed("客官，这个评论您已经点过赞了哦");
                }
            }
        }

        Comment comment = commentService.getById(commentId);
        if (Objects.isNull(comment)) {
            return CommonResult.failed("点赞失败，未获取到需要的数据，请刷新页面重试");
        }
        Integer commentGoodNumber = comment.getCommentGoodNumber();
        ++commentGoodNumber;
        if (commentService.updateById(comment.setCommentGoodNumber(commentGoodNumber))) {
            goodCommentMap.put(commentId, DateUtil.currentSeconds());
            session.setAttribute("goodCommentMap", goodCommentMap);
            return CommonResult.success("点赞成功");
        }
        return CommonResult.failed("点赞失败");
    }
}
