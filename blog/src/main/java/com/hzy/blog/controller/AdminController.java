package com.hzy.blog.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.system.HostInfo;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzy.blog.dto.ad.AdDto;
import com.hzy.blog.dto.article.ArticlePageDto;
import com.hzy.blog.dto.article.ArticleTypeUpdateDto;
import com.hzy.blog.dto.topic.TopicPageDto;
import com.hzy.blog.dto.topic.TopicTypeUpdateDto;
import com.hzy.blog.dto.user.UserDto;
import com.hzy.blog.dto.user.UserListPageDto;
import com.hzy.blog.entity.*;
import com.hzy.blog.service.*;
import com.hzy.blog.utils.CommonPage;
import com.hzy.blog.utils.CommonResult;
import com.hzy.blog.vo.AdVo;
import com.hzy.blog.vo.ArticleVo;
import com.hzy.blog.vo.TopicVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author Fa Xiaoji 14439
 * @date 2024/4/13 16:51
 */
@Controller
@RequestMapping("/hzy2003")
public class AdminController {

    @Resource
    private IArticleTypeService articleTypeService;
    @Resource
    private IArticleTagService articleTagService;
    @Resource
    private IArticleTagListService articleTagListService;
    @Resource
    private IArticleService articleService;
    @Resource
    private IUserService userService;
    @Resource
    private ILinkService linkService;
    @Resource
    private IAdService adService;
    @Resource
    private IAdTypeService adTypeService;
    @Resource
    private IAdminService adminService;
    @Resource
    private ServletContext servletContext;
    @Resource
    private IUploadFileListService uploadFileListService;
    @Resource
    private ITopicTypeService topicTypeService;
    @Resource
    private ITopicTagService topicTagService;
    @Resource
    private ITopicTagListService topicTagListService;
    @Resource
    private ITopicService topicService;

    // --------------------------------------------------管理端------------------------------------------------------------

    /**
     * 登录页面
     *
     * @return
     */
    @GetMapping("/login")
    public String adminLogin(HttpServletRequest request) {
        if (Objects.nonNull(request.getSession().getAttribute("admin"))) {
            return "redirect:/hzy2003/";
        }
        return "/admin/adminLogin";
    }

    /**
     * 管理员登录
     *
     * @param request
     * @param adminName
     * @param adminPassword
     * @param verifyCode
     * @return
     */
    @PostMapping("/adminLogin")
    @ResponseBody
    public CommonResult adminLogin(HttpServletRequest request,
                                   String adminName,
                                   String adminPassword,
                                   String verifyCode) {
        HttpSession session = request.getSession();
        if (StrUtil.isBlank(verifyCode) || !verifyCode.equals(session.getAttribute("circleCaptchaCode"))) {
            session.removeAttribute("circleCaptchaCode");
            return CommonResult.failed("验证码不正确");
        }
        Admin admin = adminService.getOne(Wrappers.<Admin>lambdaQuery()
                .eq(Admin::getAdminName, adminName)
                .eq(Admin::getAdminPassword, SecureUtil.md5(adminName + adminPassword)), false);
        if (Objects.isNull(admin)) {
            session.removeAttribute("circleCaptchaCode");
            return CommonResult.failed("用户名或者密码不正确");
        }
        session.setAttribute("admin", admin);
        return CommonResult.success("登录成功");
    }

    /**
     * 管理员退出登录
     *
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("admin");
        return "redirect:/hzy2003/login";
    }

    /**
     * 修改admin密码
     *
     * @param newPassword
     * @return
     */
    @PostMapping("/password/update")
    @ResponseBody
    public CommonResult passwordUpdate(HttpServletRequest request, String newPassword) {
        if (StrUtil.isNotBlank(newPassword)) {
            Admin admin = adminService.getOne(null, false);
            if (Objects.nonNull(admin)) {
                admin.setAdminPassword(SecureUtil.md5(admin.getAdminName() + newPassword));
                if (adminService.updateById(admin)) {
                    request.getSession().setAttribute("admin", admin);
                    return CommonResult.success("修改成功");
                }
            }
        }
        return CommonResult.failed("修改失败");
    }


    /**
     * 管理端 - 首页
     *
     * @param model
     * @return
     */
    @GetMapping("/")
    public String adminIndex(Model model) {

        // 系统信息
        OsInfo osInfo = SystemUtil.getOsInfo();
        HostInfo hostInfo = SystemUtil.getHostInfo();
        model.addAttribute("osName", osInfo.getName());
        model.addAttribute("hostAddress", hostInfo.getAddress());
//        System.out.println(hostInfo);

        // 文章数量
        long articleTypeCount = articleTypeService.count();
        long articleTagCount = articleTagListService.count();
        long articleCount = articleService.count();
        model.addAttribute("articleTypeCount", articleTypeCount);
        model.addAttribute("articleTagCount", articleTagCount);
        model.addAttribute("articleCount", articleCount);
//        System.out.println(articleTypeCount);

        // 话题数量
        long topicTypeCount = topicTypeService.count();
        long topicTagCount = topicTagListService.count();
        long topicCount = topicService.count();
        model.addAttribute("topicTypeCount", topicTypeCount);
        model.addAttribute("topicTagCount", topicTagCount);
        model.addAttribute("topicCount", topicCount);

        // 用户数量
        long userCount = userService.count();
        model.addAttribute("userCount", userCount);

        return "/admin/adminIndex";
    }

    // --------------------------------------------------用户------------------------------------------------------------

    /**
     * 管理端 - 用户列表
     *
     * @param userListPageDto
     * @param model
     * @return
     */
    @GetMapping("/user/list")
    public String userList(@Valid UserListPageDto userListPageDto, Model model) {
        Integer pageNumber = userListPageDto.getPageNumber();
        String userName = userListPageDto.getUserName();

        IPage<User> userPage = new Page<>(pageNumber, 20);
        LambdaQueryWrapper<User> userLambdaQueryWrapper = Wrappers.<User>lambdaQuery().orderByDesc(User::getUserRegisterTime);
        if (StrUtil.isNotBlank(userName)) {
            userLambdaQueryWrapper.like(User::getUserName, userName);
            model.addAttribute("userName", userName);
        }

        IPage<User> userIPage = userService.page(userPage, userLambdaQueryWrapper);
        model.addAttribute("userPage", CommonPage.restPage(userIPage));

        return "/admin/userList";
    }

    /**
     * 删除用户
     *
      * @param userId
     * @return
     */
    @PostMapping("/user/del")
    @ResponseBody
    public CommonResult userDel(String userId) {
        if (StrUtil.isBlank(userId)) {
            return CommonResult.failed("参数错误，请刷新页面重试！");
        }
        if (articleService.count(Wrappers.<Article>lambdaQuery().eq(Article::getUserId, userId)) > 0) {
            return CommonResult.failed("该用户发布过文章，无法删除，请冻结用户");
        }
        if (userService.removeById(userId)) {
            return CommonResult.success("删除成功");
        }

        return CommonResult.failed("删除失败");
    }

    /**
     * 用户修改
     *
     * @param userDto
     * @return
     */
    @PostMapping("/user/update")
    @ResponseBody
    public CommonResult userUpdate(@Valid UserDto userDto) {
        User user = userService.getById(userDto.getUserId());
        if (Objects.isNull(user)) {
            return CommonResult.failed("用户id不正确");
        }

        LocalDateTime userRegisterTime = user.getUserRegisterTime();

        String userPassword = userDto.getUserPassword();
        if (StrUtil.isNotBlank(userPassword)) {
            // 用户密码 = md5（注册时间 + 用户明文密码）
            userDto.setUserPassword(SecureUtil.md5(userRegisterTime + userPassword));
        } else {
            userDto.setUserPassword(null);
        }
        BeanUtils.copyProperties(userDto, user);

        if (userService.updateById(user)) {
            return CommonResult.success("修改成功");
        }

        return CommonResult.failed("修改失败，请重试");
    }

    // --------------------------------------------------话题------------------------------------------------------------

    /**
     * 话题类型列表，包含话题数量
     *
     * @return
     */
    @GetMapping("/topic/type/list")
    public String topicTypeList(Model model, String topicTypeParentId) {
        List<TopicType> topicType0List = topicTypeService.list(Wrappers.<TopicType>lambdaQuery()
                .isNull(TopicType::getTopicTypeParentId)
                .or().eq(TopicType::getTopicTypeParentId, "")
                .orderByAsc(TopicType::getTopicTypeSort));
        LambdaQueryWrapper<TopicType> queryWrapper = Wrappers.<TopicType>lambdaQuery()
                .isNotNull(TopicType::getTopicTypeParentId)
                .ne(TopicType::getTopicTypeParentId,"")
                .orderByAsc(TopicType::getTopicTypeSort);
        if (StrUtil.isNotBlank(topicTypeParentId)) {
            queryWrapper.eq(TopicType::getTopicTypeParentId, topicTypeParentId);
            model.addAttribute("topicTypeName", topicTypeService.getById(topicTypeParentId).getTopicTypeName());
        }
        List<TopicType> topicType1List = topicTypeService.list(queryWrapper);


        model.addAttribute("topicType0List", topicType0List);
        model.addAttribute("topicType1List", topicType1List);
        return "/admin/topicTypeList";
    }

    /**
     * 添加话题类型
     *
     * @param topicType
     * @return
     */
    @PostMapping("/topic/type/addOrUpdate")
    @ResponseBody
    public CommonResult topicTypeAdd(@Valid TopicType topicType) {
        servletContext.removeAttribute("articleTypeList");
        String topicTypeId = topicType.getTopicTypeId();
        if(StrUtil.isNotBlank(topicType.getTopicTypeParentId()) && StrUtil.isNotBlank(topicType.getTopicTypeId()) && topicType.getTopicTypeParentId().equals(topicType.getTopicTypeId())){
            return CommonResult.failed("不能将自己分配到自己的目录下");
        }

        if (StrUtil.isBlank(topicTypeId)) {
            topicType.setTopicTypeAddTime(LocalDateTime.now());
            if (topicTypeService.save(topicType)) {

                return CommonResult.success("添加成功");
            }
        }
        if (topicTypeService.updateById(topicType)) {
            return CommonResult.success("修改成功");
        }

        return CommonResult.failed("操作失败");
    }

    /**
     * 修改话题类型
     *
     * @param topicTypeUpdateDto
     * @return
     */
    @PostMapping("/topic/type/update")
    @ResponseBody
    public CommonResult topicTypeUpdate(@Valid TopicTypeUpdateDto topicTypeUpdateDto) {
        TopicType topicType = new TopicType();
        BeanUtils.copyProperties(topicTypeUpdateDto, topicType);

        String topicTypeName = topicType.getTopicTypeName();
        Integer articleTypeSort = topicType.getTopicTypeSort();
        if (StrUtil.isBlank(topicTypeName)) {
            topicType.setTopicTypeName(null);
        }
        if (Objects.isNull(topicTypeName)) {
            topicType.setTopicTypeSort(null);
        }
        if(StrUtil.isNotBlank(topicType.getTopicTypeParentId()) && StrUtil.isNotBlank(topicType.getTopicTypeId()) && topicType.getTopicTypeParentId().equals(topicType.getTopicTypeId())){
            return CommonResult.failed("不能将自己分配到自己的目录下");
        }

        if (topicTypeService.updateById(topicType)) {
            servletContext.removeAttribute("topicTypeList");
            return CommonResult.success("添加成功");
        }
        return CommonResult.failed("添加失败");
    }

    /**
     * 删除话题分类
     *
     * @param topicTypeId
     * @return
     */
    @PostMapping("/topic/type/del")
    @ResponseBody
    public CommonResult topicTypeDel(@NotBlank(message = "话题分类id 不能为空") String topicTypeId) {
        if (topicService.count(Wrappers.<Topic>lambdaQuery()
                .eq(Topic::getTopicTypeId, topicTypeId)) > 0) {
            return CommonResult.failed("请先删除该分类下的话题");
        }

        if (topicTypeService.count(Wrappers.<TopicType>lambdaQuery().eq(TopicType::getTopicTypeParentId, topicTypeId)) > 0) {
            return CommonResult.failed("请先删除下级分类");
        }

        if (topicTypeService.removeById(topicTypeId)) {
            servletContext.removeAttribute("topicTypeList");
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }

    /**
     * 话题标签列
     *
     * @param model
     * @return
     */
    @GetMapping("/topic/tag/list")
    public String topicTagList(Model model) {
        List<TopicTag> topicTagList =
                topicTagService.list(Wrappers.<TopicTag>lambdaQuery().orderByDesc(TopicTag::getTopicTagAddTime));
        model.addAttribute("topicTagList", topicTagList);
        return "/admin/topicTagList";
    }

    /**
     * 话题标签 添加
     *
     * @param topicTag
     * @return
     */
    @PostMapping("/topic/tag/addOrUpdate")
    @ResponseBody
    public CommonResult topicTagAddOrUpdate(TopicTag topicTag) {
        String topicTagId = topicTag.getTopicTagId();
        if(StrUtil.isNotBlank(topicTagId)) {
            if(topicTagService.updateById((topicTag))) {
                return CommonResult.success("修改成功");
            }
            return CommonResult.failed("修改失败");
        }

        topicTag.setTopicTagAddTime(LocalDateTime.now());
        if(topicTagService.save(topicTag)) {
            servletContext.removeAttribute("topicTagList");
            return CommonResult.success("话题标签添加成功");
        }
        return CommonResult.failed("话题标签添加失败");
    }

    /**
     * 话题标签 删除
     *
     * @param topicTagId
     * @return
     */
    @PostMapping("/topic/tag/del")
    @ResponseBody
    public CommonResult topicTagDel(String topicTagId) {
        if(StrUtil.isBlank(topicTagId)) {
            return CommonResult.failed("删除失败，没有获取到话题标签id");
        }

        if(topicTagListService.count(Wrappers.<TopicTagList>lambdaQuery()
                .eq(TopicTagList::getTopicTagId, topicTagId)) > 0) {
            return CommonResult.failed("该话题标签已经被使用，请先删除关联话题");
        }

        if(topicTagService.removeById(topicTagId)) {
            servletContext.removeAttribute("topicTagList");
            return CommonResult.success("话题标签删除成功");
        }
        return CommonResult.failed("话题标签删除失败");
    }

    /**
     * 话题列表
     *
     * @param topicPageDto
     * @return
     */
    @GetMapping("/topic/list")
    public String topicList(@Valid TopicPageDto topicPageDto, Model model) {
        IPage<TopicVo> topicVoPage = new Page<>(topicPageDto.getPageNumber(), 20);
        IPage<TopicVo> topicVoIPage = topicService.topicList(topicVoPage, topicPageDto.getTopicTitle(), null);
        model.addAttribute("topicVoIPage", CommonPage.restPage(topicVoIPage));
        if(StrUtil.isNotBlank(topicPageDto.getTopicTitle())) {
            model.addAttribute("topicTitle", topicPageDto.getTopicTitle());
        }
        return "/admin/topicList";
    }

    /**
     * 设置为热门话题
     *
     * @param topicId
     * @return
     */
    @PostMapping("/topic/hot")
    @ResponseBody
    public CommonResult topicHot(String topicId) {
        if (topicService.update(Wrappers.<Topic>lambdaUpdate().eq(Topic::getTopicId,topicId).set(Topic::getTopicHot,1))) {
            servletContext.removeAttribute("topicHotList");
            return CommonResult.success("设置成功");
        }
        return CommonResult.failed("设置失败");
    }

    /**
     * 话题删除
     *
     * @param topicId
     * @return
     */
    @PostMapping("/topic/del")
    @ResponseBody
    public CommonResult topicDel(String topicId) {
        return topicService.delTopic(topicId);
    }

    // --------------------------------------------------文章------------------------------------------------------------

    /**
     * 文章类型列表，包含文章数量
     *
     * @return
     */
    @GetMapping("/article/type/list")
    public String articleTypeList(Model model, String articleTypeParentId) {
        List<ArticleType> articleType0List = articleTypeService.list(Wrappers.<ArticleType>lambdaQuery()
                .isNull(ArticleType::getArticleTypeParentId)
                .or().eq(ArticleType::getArticleTypeParentId, "")
                .orderByAsc(ArticleType::getArticleTypeSort));
        LambdaQueryWrapper<ArticleType> queryWrapper = Wrappers.<ArticleType>lambdaQuery()
                .isNotNull(ArticleType::getArticleTypeParentId)
                .ne(ArticleType::getArticleTypeParentId,"")
                .orderByAsc(ArticleType::getArticleTypeSort);
        if (StrUtil.isNotBlank(articleTypeParentId)) {
            queryWrapper.eq(ArticleType::getArticleTypeParentId, articleTypeParentId);
            model.addAttribute("articleTypeName", articleTypeService.getById(articleTypeParentId).getArticleTypeName());
        }
        List<ArticleType> articleType1List = articleTypeService.list(queryWrapper);


        model.addAttribute("articleType0List", articleType0List);
        model.addAttribute("articleType1List", articleType1List);
        return "/admin/articleTypeList";
    }

    /**
     * 添加文章类型
     *
     * @param articleType
     * @return
     */
    @PostMapping("/article/type/addOrUpdate")
    @ResponseBody
    public CommonResult articleTypeAdd(@Valid ArticleType articleType) {
        servletContext.removeAttribute("articleTypeList");
        String articleTypeId = articleType.getArticleTypeId();
        if(StrUtil.isNotBlank(articleType.getArticleTypeParentId()) && StrUtil.isNotBlank(articleType.getArticleTypeId()) && articleType.getArticleTypeParentId().equals(articleType.getArticleTypeId())){
            return CommonResult.failed("不能将自己分配到自己的目录下");
        }

        if (StrUtil.isBlank(articleTypeId)) {
            articleType.setArticleTypeAddTime(LocalDateTime.now());
            if (articleTypeService.save(articleType)) {

                return CommonResult.success("添加成功");
            }
        }
        if (articleTypeService.updateById(articleType)) {
            return CommonResult.success("修改成功");
        }

        return CommonResult.failed("操作失败");
    }

    /**
     * 修改文章类型
     *
     * @param articleTypeUpdateDto
     * @return
     */
    @PostMapping("/article/type/update")
    @ResponseBody
    public CommonResult articleTypeUpdate(@Valid ArticleTypeUpdateDto articleTypeUpdateDto) {
        ArticleType articleType = new ArticleType();
        BeanUtils.copyProperties(articleTypeUpdateDto, articleType);

        String articleTypeName = articleType.getArticleTypeName();
        Integer articleTypeSort = articleType.getArticleTypeSort();
        if (StrUtil.isBlank(articleTypeName)) {
            articleType.setArticleTypeName(null);
        }
        if (Objects.isNull(articleTypeSort)) {
            articleType.setArticleTypeSort(null);
        }
        if(StrUtil.isNotBlank(articleType.getArticleTypeParentId()) && StrUtil.isNotBlank(articleType.getArticleTypeId()) && articleType.getArticleTypeParentId().equals(articleType.getArticleTypeId())){
            return CommonResult.failed("不能将自己分配到自己的目录下");
        }

        if (articleTypeService.updateById(articleType)) {
            servletContext.removeAttribute("articleTypeList");
            return CommonResult.success("添加成功");
        }
        return CommonResult.failed("添加失败");
    }

    /**
     * 删除文章分类
     *
     * @param articleTypeId
     * @return
     */
    @PostMapping("/article/type/del")
    @ResponseBody
    public CommonResult articleTypeDel(@NotBlank(message = "文章分类id 不能为空") String articleTypeId) {
        if (articleService.count(Wrappers.<Article>lambdaQuery()
                .eq(Article::getArticleTypeId, articleTypeId)) > 0) {
            return CommonResult.failed("请先删除该分类下的文章");
        }

        if (articleTypeService.count(Wrappers.<ArticleType>lambdaQuery().eq(ArticleType::getArticleTypeParentId, articleTypeId)) > 0) {
            return CommonResult.failed("请先删除下级分类");
        }

        if (articleTypeService.removeById(articleTypeId)) {
            servletContext.removeAttribute("articleTypeList");
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }

    /**
     * 文章标签列
     *
     * @param model
     * @return
     */
    @GetMapping("/article/tag/list")
    public String articleTagList(Model model) {
        List<ArticleTag> articleTagList =
                articleTagService.list(Wrappers.<ArticleTag>lambdaQuery().orderByDesc(ArticleTag::getArticleTagAddTime));
        model.addAttribute("articleTagList", articleTagList);
        return "/admin/articleTagList";
    }

    /**
     * 文章标签 添加
     *
     * @param articleTag
     * @return
     */
    @PostMapping("/article/tag/addOrUpdate")
    @ResponseBody
    public CommonResult articleTagAddOrUpdate(ArticleTag articleTag) {
        String articleTagId = articleTag.getArticleTagId();
        if(StrUtil.isNotBlank(articleTagId)) {
            if(articleTagService.updateById((articleTag))) {
                return CommonResult.success("修改成功");
            }
            return CommonResult.failed("修改失败");
        }

        articleTag.setArticleTagAddTime(LocalDateTime.now());
        if(articleTagService.save(articleTag)) {
            servletContext.removeAttribute("articleTagList");
            return CommonResult.success("文章标签添加成功");
        }
        return CommonResult.failed("文章标签添加失败");
    }

    /**
     * 文章标签 删除
     *
     * @param articleTagId
     * @return
     */
    @PostMapping("/article/tag/del")
    @ResponseBody
    public CommonResult articleTagDel(String articleTagId) {
        if(StrUtil.isBlank(articleTagId)) {
            return CommonResult.failed("删除失败，没有获取到文章标签id");
        }

        if(articleTagListService.count(Wrappers.<ArticleTagList>lambdaQuery()
                .eq(ArticleTagList::getArticleTagId, articleTagId)) > 0) {
            return CommonResult.failed("该文章标签已经被使用，请先删除关联文章");
        }

        if(articleTagService.removeById(articleTagId)) {
            servletContext.removeAttribute("articleTagList");
            return CommonResult.success("文章标签删除成功");
        }
        return CommonResult.failed("文章标签删除失败");
    }

    /**
     * 文章列表
     *
     * @param articlePageDto
     * @return
     */
    @GetMapping("/article/list")
    public String articleList(@Valid ArticlePageDto articlePageDto, Model model) {
        IPage<ArticleVo> articleVoPage = new Page<>(articlePageDto.getPageNumber(), 20);
        IPage<ArticleVo> articleVoIPage = articleService.articleList(articleVoPage, articlePageDto.getArticleTitle(), null);
        model.addAttribute("articleVoIPage", CommonPage.restPage(articleVoIPage));
        if(StrUtil.isNotBlank(articlePageDto.getArticleTitle())) {
            model.addAttribute("articleTitle", articlePageDto.getArticleTitle());
        }
        return "/admin/articleList";
    }

    /**
     * 设置为热门文章
     *
     * @param articleId
     * @return
     */
    @PostMapping("/article/hot")
    @ResponseBody
    public CommonResult articleHot(String articleId) {
        if (articleService.update(Wrappers.<Article>lambdaUpdate().eq(Article::getArticleId,articleId).set(Article::getArticleHot,1))) {
            servletContext.removeAttribute("articleHotList");
            return CommonResult.success("设置成功");
        }
        return CommonResult.failed("设置失败");
    }

    /**
     * 文章删除
     *
     * @param articleId
     * @return
     */
    @PostMapping("/article/del")
    @ResponseBody
    public CommonResult articleDel(String articleId) {
        return articleService.delArticle(articleId);
    }

    // --------------------------------------------------友联------------------------------------------------------------

    /**
     * 友情连接列表页面
     *
     * @return
     */
    @GetMapping("/link/list")
    public String linkList(Model model) {
        List<Link> linkList = linkService.list(Wrappers.<Link>lambdaQuery().orderByAsc(Link::getLinkSort));
        model.addAttribute("linkList", linkList);
        return "/admin/linklist";
    }

    /**
     * 更新友联
     *
     * @param link
     * @return
     */
    @PostMapping("/link/addOrUpdate")
    @ResponseBody
    public CommonResult linkAddOrUpdate(Link link) {
        String linkId = link.getLinkId();
        if(StrUtil.isBlank(linkId)) {
            // 添加友联
            link.setLinkAddTime(LocalDateTime.now());
            if(linkService.save(link)) {
                servletContext.removeAttribute("linkList");
                return CommonResult.success("添加成功");
            }
            return CommonResult.failed("添加失败");
        }
        if(linkService.updateById(link)) {
            return CommonResult.success("更新成功");
        }
        return CommonResult.failed("更新失败");
    }

    /**
     * 删除友联
     *
     * @param linkId
     * @return
     */
    @PostMapping("/link/del")
    @ResponseBody
    public CommonResult linkDel(String linkId) {
        if(linkService.removeById(linkId)) {
            servletContext.removeAttribute("linkList");
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }

    // --------------------------------------------------广告------------------------------------------------------------

    /**
     * 广告管理
     *
     * @param adTypeId
     * @param model
     * @return
     */
    @GetMapping("/ad/list")
    public String adList(String adTypeId, Model model) {
        List<AdType> adTypeList = adTypeService.list(Wrappers.<AdType>lambdaQuery()
                .orderByAsc(AdType::getAdTypeSort));
        model.addAttribute("adTypeList", adTypeList);

        List<AdVo> adVoList = adService.adList(adTypeId);
        model.addAttribute("adVoList", adVoList);

        return "/admin/adList";
    }

    /**
     * 广告类型管理
     *
     * @param adType
     * @return
     */
    @PostMapping("/ad/type/addOrUpdate")
    @ResponseBody
    public CommonResult adTypeAddOrUpdate(AdType adType) {
        String adTypeId = adType.getAdTypeId();
        if (StrUtil.isBlank(adTypeId)) {
            //添加广告类型
            adType.setAdTypeAddTime(DateUtil.date());
            if (adTypeService.save(adType)) {
                return CommonResult.success("添加成功");
            }
            return CommonResult.success("添加失败");
        }

        //修改广告类型
        if (adTypeService.updateById(adType)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    /**
     * 广告管理
     *
     * @param adDto
     * @return
     */
    @PostMapping("/ad/addOrUpdate")
    @ResponseBody
    public CommonResult adAddOrUpdate(AdDto adDto, MultipartFile file) throws IOException {
        if (Objects.nonNull(file)) {
//            判断是否上传的图片，是否是我们指定的像素
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (Objects.isNull(read)) {
                return CommonResult.failed("请上传图片文件");
            }
            int width = read.getWidth();
            int height = read.getHeight();
            if (width != 850 || height != 100) {
                return CommonResult.failed("图片的像素为 850px * 100px");
            }

            adDto.setAdImgUrl(uploadFileListService.getUploadFileUrl(file));
        }

        String adId = adDto.getAdId();
        Ad ad = new Ad();
        BeanUtils.copyProperties(adDto, ad);
        ad.setAdBeginTime(DateUtil.parseDateTime(adDto.getAdBeginTime()));
        ad.setAdEndTime(DateUtil.parseDateTime(adDto.getAdEndTime()));

        //移除首页广告缓存
        servletContext.removeAttribute("adIndexList");
        servletContext.removeAttribute("adArticleList");

        if (StrUtil.isBlank(adId)) {
            //添加广告类型
            ad.setAdAddTime(DateUtil.date());
            if (adService.save(ad)) {
                return CommonResult.success("添加成功");
            }
            return CommonResult.success("添加失败");
        }

        //修改广告类型
        if (adService.updateById(ad)) {
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改失败");
    }

    /**
     * 删除广告
     *
     * @param adId
     * @return
     */
    @PostMapping("/ad/del")
    @ResponseBody
    public CommonResult adDel(String adId) {
        if (adService.removeById(adId)) {
            servletContext.removeAttribute("adIndexList");
            servletContext.removeAttribute("adArticleList");
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }


}
