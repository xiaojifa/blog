package com.hzy.blog.service;

import com.hzy.blog.entity.ArticleType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzy.blog.vo.ArticleTypeTreeVo;
import com.hzy.blog.vo.ArticleTypeVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzy
 * @since 2024-04-13
 */
public interface IArticleTypeService extends IService<ArticleType> {

    /**
     * 文章类型列表，包含文章数量
     * @return
     */
    List<ArticleTypeVo> articleTypeList();

    /**
     * 获取首页文章类型树形目录
     * @param articleTypeParentId
     * @return
     */
    List<ArticleTypeTreeVo> getIndexArticleTypeList(String articleTypeParentId);
}
