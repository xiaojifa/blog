package com.hzy.blog.intercepter;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hzy.blog.entity.*;
import com.hzy.blog.service.*;
import com.hzy.blog.vo.ArticleTypeTreeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 14439
 */
@Slf4j
public class GlobalIntercepter implements HandlerInterceptor {
    @Resource
    private IArticleService articleService;
    @Resource
    private IArticleTypeService articleTypeService;
    @Resource
    private IArticleTagService articleTagService;
    @Resource
    private IAdTypeService adTypeService;
    @Resource
    private IAdService adService;
    @Resource
    private ILinkService linkService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        ServletContext servletContext = request.getServletContext();

        //获取首页文章类型树形目录
        List<ArticleTypeTreeVo> articleTypeList = (List<ArticleTypeTreeVo>) servletContext.getAttribute("articleTypeList");
        if (CollUtil.isEmpty(articleTypeList)) {
            articleTypeList = articleTypeService.getIndexArticleTypeList(null);
            if (CollUtil.isNotEmpty(articleTypeList)) {
                for (ArticleTypeTreeVo articleTypeTreeVo : articleTypeList) {
                    articleTypeTreeVo.setArticleTypeTreeVoList(articleTypeService.getIndexArticleTypeList(articleTypeTreeVo.getArticleTypeId()));
                }
            }

            //首页最新文章
            for (ArticleTypeTreeVo articleTypeTreeVo : articleTypeList) {
                List<ArticleTypeTreeVo> articleTypeTreeVoList = articleTypeTreeVo.getArticleTypeTreeVoList();
                if(CollUtil.isNotEmpty(articleTypeTreeVoList)){
                    LambdaQueryWrapper<Article> queryWrapper = Wrappers.<Article>lambdaQuery()
                            .in(Article::getArticleTypeId, articleTypeTreeVoList.stream().map(ArticleTypeTreeVo::getArticleTypeId).collect(Collectors.toList()))
                            .select(Article::getArticleId,
                                    Article::getArticleLookNumber,
                                    Article::getArticleGoodNumber,
                                    Article::getArticleCollectionNumber,
                                    Article::getArticleCoverUrl,
                                    Article::getArticleAddTime,
                                    Article::getArticleTitle)
                            .orderByDesc(Article::getArticleAddTime)
                            .last(" limit 6");
                    articleTypeTreeVo.setArticleList(articleService.list(queryWrapper));
                }
            }
            servletContext.setAttribute("articleTypeList", articleTypeList);
        }

        //热门文章
        List<Article> articleHotList = (List<Article>) servletContext.getAttribute("articleHotList");
        if (CollUtil.isEmpty(articleHotList)) {
            articleHotList = articleService.list(Wrappers.<Article>lambdaQuery().eq(Article::getArticleHot, 1).select(Article::getArticleId, Article::getArticleTitle, Article::getArticleAddTime).last(" limit 5"));
            servletContext.setAttribute("articleHotList", articleHotList);
        }

        //热门标签
        List<ArticleTag> articleTagList = (List<ArticleTag>) servletContext.getAttribute("articleTagList");
        if (CollUtil.isEmpty(articleTagList)) {
            articleTagList = articleTagService.list(Wrappers.<ArticleTag>lambdaQuery().select(ArticleTag::getArticleTagId, ArticleTag::getArticleTagName));
            servletContext.setAttribute("articleTagList", articleTagList);
        }

        //广告
        List<Ad> adIndexList = (List<Ad>) servletContext.getAttribute("adIndexList");
        if (CollUtil.isEmpty(adIndexList)) {
            AdType homeAd = adTypeService.getOne(Wrappers.<AdType>lambdaQuery().eq(AdType::getAdTypeTag, "homeAd").select(AdType::getAdTypeId), false);
            if (Objects.nonNull(homeAd)) {
                DateTime date = DateUtil.date();
                adIndexList = adService.list(Wrappers.<Ad>lambdaQuery().eq(Ad::getAdTypeId, homeAd.getAdTypeId()).lt(Ad::getAdBeginTime, date).gt(Ad::getAdEndTime, date).select(Ad::getAdId, Ad::getAdImgUrl, Ad::getAdLinkUrl, Ad::getAdTitle).orderByAsc(Ad::getAdSort));
                servletContext.setAttribute("adIndexList", adIndexList);
            }
        }

        //友情连接
        List<Link> linkList = (List<Link>) servletContext.getAttribute("linkList");
        if (CollUtil.isEmpty(linkList)) {
            linkList = linkService.list(Wrappers.<Link>lambdaQuery().orderByAsc(Link::getLinkSort));
            servletContext.setAttribute("linkList", linkList);
        }

        return true;
    }
}
