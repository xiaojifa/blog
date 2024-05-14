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
        // 判断session中是否存在名为"admin"的属性
        if (Objects.nonNull(request.getSession().getAttribute("admin"))) {
            // 存在则重定向到"/hzy2003/"路径 管理员端界面
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
        // 判断验证码是否为空或与session中的验证码不匹配
        if (StrUtil.isBlank(verifyCode) || !verifyCode.equals(session.getAttribute("circleCaptchaCode"))) {
            // 移除验证码
            session.removeAttribute("circleCaptchaCode");
            return CommonResult.failed("验证码不正确");
        }
        // 调用adminService的getOne方法查询数据库中是否存在该管理员
        Admin admin = adminService.getOne(Wrappers.<Admin>lambdaQuery()
                .eq(Admin::getAdminName, adminName)
                .eq(Admin::getAdminPassword, SecureUtil.md5(adminName + adminPassword)), false);
        if (Objects.isNull(admin)) {
            session.removeAttribute("circleCaptchaCode");
            return CommonResult.failed("用户名或者密码不正确");
        }
        // 在session中给admin赋值admin
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
        // 从session中移除admin登录
        request.getSession().removeAttribute("admin");
        // 重定向到登陆界面
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
        // 判断新密码是否为空
        if (StrUtil.isNotBlank(newPassword)) {
            // 调用adminService的getOne方法获取当前登录的管理员信息
            Admin admin = adminService.getOne(null, false);
            if (Objects.nonNull(admin)) {
                // 将新密码进行MD5加密后更新到管理员信息中
                admin.setAdminPassword(SecureUtil.md5(admin.getAdminName() + newPassword));
                // 调用adminService的updateById方法更新数据库中的管理员信息
                if (adminService.updateById(admin)) {
                    // 将更新后的管理员信息存入session中
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
        // 将操作系统的名称添加到模型中
        // model是一个对象，用于存储视图所需的数据；addAttribute是Model接口中的一个方法，用于向模型中添加属性
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

        // 从UserListPageDto对象中获取页码和用户名信息
        Integer pageNumber = userListPageDto.getPageNumber();
        String userName = userListPageDto.getUserName();

        // 创建一个IPage<User>对象，指定每页显示20条记录
        IPage<User> userPage = new Page<>(pageNumber, 20);
        // 使用LambdaQueryWrapper<User>构建查询条件
        LambdaQueryWrapper<User> userLambdaQueryWrapper = Wrappers.<User>lambdaQuery()
                // 按照用户注册时间降序排列
                .orderByDesc(User::getUserRegisterTime);
        // 检查用户名是否为空
        if (StrUtil.isNotBlank(userName)) {
            // 在查询条件中添加模糊匹配用户名的条件
            userLambdaQueryWrapper.like(User::getUserName, userName);
            // 将用户名添加到模型中
            model.addAttribute("userName", userName);
        }
        // 调用userService.page()方法执行分页查询
        IPage<User> userIPage = userService.page(userPage, userLambdaQueryWrapper);
        // 将分页结果添加到模型中
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
        // 检查userId是否为空
        if (StrUtil.isBlank(userId)) {
            return CommonResult.failed("参数错误，请刷新页面重试！");
        }
        // 使用articleService.count()方法查询该用户发布过的文章数量是否>0
        if (articleService.count(Wrappers.<Article>lambdaQuery()
                .eq(Article::getUserId, userId)) > 0) {
            return CommonResult.failed("该用户发布过文章，无法删除，请冻结用户");
        }
        // 调用userService.removeById()方法根据userId删除用户
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
        // 根据userDto中的用户ID获取用户信息
        User user = userService.getById(userDto.getUserId());
        // 判断用户是否存在
        if (Objects.isNull(user)) {
            return CommonResult.failed("用户id不正确");
        }
        // 获取用户的注册时间
        LocalDateTime userRegisterTime = user.getUserRegisterTime();

        String userPassword = userDto.getUserPassword();
        // 判断用户密码是否为空
        if (StrUtil.isNotBlank(userPassword)) {
            // 将用户密码进行md5加密
            // 用户密码 = md5（注册时间 + 用户明文密码）
            userDto.setUserPassword(SecureUtil.md5(userRegisterTime + userPassword));
        } else {
            userDto.setUserPassword(null);
        }
        // 使用BeanUtils.copyProperties()方法将userDto中的属性值复制到user对象中
        BeanUtils.copyProperties(userDto, user);
        // 调用userService.updateById()方法更新用户信息
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
        // 使用topicTypeService.list()方法查询所有一级话题类型
        List<TopicType> topicType0List = topicTypeService.list(Wrappers.<TopicType>lambdaQuery()
                .isNull(TopicType::getTopicTypeParentId)
                .or().eq(TopicType::getTopicTypeParentId, "")
                // 按照排序升序排列
                .orderByAsc(TopicType::getTopicTypeSort));
        // 使用Wrappers.<TopicType>lambdaQuery()构建查询条件查询所有二级话题类型
        LambdaQueryWrapper<TopicType> queryWrapper = Wrappers.<TopicType>lambdaQuery()
                .isNotNull(TopicType::getTopicTypeParentId)
                .ne(TopicType::getTopicTypeParentId,"")
                // 按照排序升序排列
                .orderByAsc(TopicType::getTopicTypeSort);
        // 判断话题类型父id是否为空
        if (StrUtil.isNotBlank(topicTypeParentId)) {
            // 在查询条件中添加父级话题类型ID等于topicTypeParentId的条件
            queryWrapper.eq(TopicType::getTopicTypeParentId, topicTypeParentId);
            // 将该话题类型的名称添加到模型中
            model.addAttribute("topicTypeName", topicTypeService.getById(topicTypeParentId).getTopicTypeName());
        }
        List<TopicType> topicType1List = topicTypeService.list(queryWrapper);

        // 将一级话题类型列表和二级话题类型列表添加到模型中
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
        // 从servletContext中移除名为"articleTypeList"的属性
        servletContext.removeAttribute("articleTypeList");
        // 从topicType对象中获取话题类型ID和父级话题类型ID
        String topicTypeId = topicType.getTopicTypeId();
        if(StrUtil.isNotBlank(topicType.getTopicTypeParentId()) && StrUtil.isNotBlank(topicType.getTopicTypeId()) && topicType.getTopicTypeParentId().equals(topicType.getTopicTypeId())){
            return CommonResult.failed("不能将自己分配到自己的目录下");
        }
        // 判断话题类型ID是否为空
        if (StrUtil.isBlank(topicTypeId)) {
            // 设置话题类型的添加时间为当前时间
            topicType.setTopicTypeAddTime(LocalDateTime.now());
            // 调用topicTypeService.save()方法保存话题类型
            if (topicTypeService.save(topicType)) {

                return CommonResult.success("添加成功");
            }
        }
        // 调用topicTypeService.updateById()方法更新话题类型
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
        // 使用BeanUtils.copyProperties()方法将topicTypeUpdateDto中的属性值复制到一个新的TopicType对象中
        TopicType topicType = new TopicType();
        BeanUtils.copyProperties(topicTypeUpdateDto, topicType);

        // 从topicType对象中获取话题类型名称和排序
        String topicTypeName = topicType.getTopicTypeName();
        Integer articleTypeSort = topicType.getTopicTypeSort();
        // 判断类型名称是否为空
        if (StrUtil.isBlank(topicTypeName)) {
            topicType.setTopicTypeName(null);
        }
        // 判断排序是否为null
        if (Objects.isNull(articleTypeSort)) {
            topicType.setTopicTypeSort(null);
        }
        // 判断父级话题类型ID和话题类型ID是否相同
        if(StrUtil.isNotBlank(topicType.getTopicTypeParentId()) && StrUtil.isNotBlank(topicType.getTopicTypeId()) && topicType.getTopicTypeParentId().equals(topicType.getTopicTypeId())){
            return CommonResult.failed("不能将自己分配到自己的目录下");
        }
        // 调用topicTypeService.updateById()方法更新话题类型
        if (topicTypeService.updateById(topicType)) {
            // 从servletContext中移除名为"topicTypeList"的属性
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
        // 使用topicService.count()方法查询该分类下的话题数量 > 0
        if (topicService.count(Wrappers.<Topic>lambdaQuery()
                .eq(Topic::getTopicTypeId, topicTypeId)) > 0) {
            return CommonResult.failed("请先删除该分类下的话题");
        }

        // 使用topicTypeService.count()方法查询该分类下的下级分类数量
        if (topicTypeService.count(Wrappers.<TopicType>lambdaQuery().eq(TopicType::getTopicTypeParentId, topicTypeId)) > 0) {
            return CommonResult.failed("请先删除下级分类");
        }

        // 调用topicTypeService.removeById()方法根据topicTypeId删除话题分类
        if (topicTypeService.removeById(topicTypeId)) {
            // 从servletContext中移除名为"topicTypeList"的属性
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
        // 使用topicTagService.list()方法查询所有话题标签
        List<TopicTag> topicTagList =
                topicTagService.list(Wrappers.<TopicTag>lambdaQuery()
                        // 按照添加时间降序排列
                        .orderByDesc(TopicTag::getTopicTagAddTime));
        // 将查询结果添加到模型中
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
        //从topicTag对象中获取话题标签ID
        String topicTagId = topicTag.getTopicTagId();
        // 判断话题标签ID是否为空
        if(StrUtil.isNotBlank(topicTagId)) {
            // 调用topicTagService.updateById()方法更新话题标签
            if(topicTagService.updateById((topicTag))) {
                return CommonResult.success("修改成功");
            }
            return CommonResult.failed("修改失败");
        }
        // 设置话题标签的添加时间为当前时间
        topicTag.setTopicTagAddTime(LocalDateTime.now());
        // 调用topicTagService.save()方法保存话题标签
        if(topicTagService.save(topicTag)) {
            // 从servletContext中移除名为"topicTagList"的属性
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
        // 判断topicTagId是否为空
        if(StrUtil.isBlank(topicTagId)) {
            return CommonResult.failed("删除失败，没有获取到话题标签id");
        }

        // 使用topicTagListService.count()方法查询该话题标签关联的话题数量 > 0
        if(topicTagListService.count(Wrappers.<TopicTagList>lambdaQuery()
                .eq(TopicTagList::getTopicTagId, topicTagId)) > 0) {
            return CommonResult.failed("该话题标签已经被使用，请先删除关联话题");
        }

        // 调用topicTagService.removeById()方法根据topicTagId删除话题标签
        if(topicTagService.removeById(topicTagId)) {
            // 从servletContext中移除名为"topicTagList"的属性
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
        // 创建一个分页对象topicVoPage，设置每页显示20条数据
        IPage<TopicVo> topicVoPage = new Page<>(topicPageDto.getPageNumber(), 20);
        // 调用topicService.topicList()方法查询话题列表
        IPage<TopicVo> topicVoIPage = topicService.topicList(topicVoPage, topicPageDto.getTopicTitle(), null);
        // 将结果存储在topicVoIPage中
        model.addAttribute("topicVoIPage", CommonPage.restPage(topicVoIPage));
        // 将topicVoIPage转换为通用分页对象判断是否为空
        if(StrUtil.isNotBlank(topicPageDto.getTopicTitle())) {
            // 将其添加到模型中
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
        // 使用topicService.update()方法更新话题的热门状态为1
        if (topicService.update(Wrappers.<Topic>lambdaUpdate().eq(Topic::getTopicId,topicId).set(Topic::getTopicHot,1))) {
            // 从servletContext中移除名为"topicHotList"的属性
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
        // 调用topicService的删除方法
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
        // 使用linkService.list()方法查询所有友情连接
        List<Link> linkList = linkService.list(Wrappers.<Link>lambdaQuery()
                // 按照排序升序排列
                .orderByAsc(Link::getLinkSort));
        // 将查询结果添加到模型中
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
        // 从link对象中获取友情连接ID
        String linkId = link.getLinkId();
        // 判断友联id是否为空
        if(StrUtil.isBlank(linkId)) {
            // 添加友联
            link.setLinkAddTime(LocalDateTime.now());
            // 调用linkService.save()方法保存友情连接
            if(linkService.save(link)) {
                // 从servletContext中移除名为"linkList"的属性
                servletContext.removeAttribute("linkList");
                return CommonResult.success("添加成功");
            }
            return CommonResult.failed("添加失败");
        }
        // 调用linkService.updateById()方法更新友情连接
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
        // 调用linkService的通过id删除的方法
        if(linkService.removeById(linkId)) {
            // 从servletContext中移除名为"linkList"的属性
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
        // 调用adTypeService.list方法查询所有广告类型
        List<AdType> adTypeList = adTypeService.list(Wrappers.<AdType>lambdaQuery()
                // 升序排序
                .orderByAsc(AdType::getAdTypeSort));
        // 查询结果添加到模型中
        model.addAttribute("adTypeList", adTypeList);

        // 调用adService.adList()方法根据adTypeId查询广告列表
        List<AdVo> adVoList = adService.adList(adTypeId);
        // 将查询结果添加到模型中
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
        // 从adType对象中获取广告类型ID
        String adTypeId = adType.getAdTypeId();
        if (StrUtil.isBlank(adTypeId)) {
            // 设置广告类型的添加时间为当前时间
            adType.setAdTypeAddTime(DateUtil.date());
            // 调用adTypeService.save()方法保存广告类型
            if (adTypeService.save(adType)) {
                return CommonResult.success("添加成功");
            }
            return CommonResult.success("添加失败");
        }

        // 调用adTypeService.updateById()方法更新广告类型
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

        // 根据adDto对象的属性值创建一个新的Ad对象
        String adId = adDto.getAdId();
        Ad ad = new Ad();
        // 将adDto对象的属性值复制到新创建的Ad对象中
        BeanUtils.copyProperties(adDto, ad);
        // 将adDto对象的adBeginTime和adEndTime属性值转换为Date类型  设置到新创建的Ad对象的相应属性中
        ad.setAdBeginTime(DateUtil.parseDateTime(adDto.getAdBeginTime()));
        ad.setAdEndTime(DateUtil.parseDateTime(adDto.getAdEndTime()));

        //移除首页广告缓存
        servletContext.removeAttribute("adIndexList");
        servletContext.removeAttribute("adArticleList");

        // 根据adId属性值判断是添加广告还是更新广告
        if (StrUtil.isBlank(adId)) {
            // 添加广告类型
            // 设置广告的添加时间为当前时间
            ad.setAdAddTime(DateUtil.date());
            // 调用adService.save()方法保存广告
            if (adService.save(ad)) {
                return CommonResult.success("添加成功");
            }
            return CommonResult.success("添加失败");
        }

        // 用adService.updateById()方法更新广告
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
        // 调用adService.removeById()方法根据adId删除广告
        if (adService.removeById(adId)) {
            // 从servletContext中移除名为"adIndexList"和"adArticleList"的属性
            servletContext.removeAttribute("adIndexList");
            servletContext.removeAttribute("adArticleList");
            return CommonResult.success("删除成功");
        }
        return CommonResult.failed("删除失败");
    }


}
