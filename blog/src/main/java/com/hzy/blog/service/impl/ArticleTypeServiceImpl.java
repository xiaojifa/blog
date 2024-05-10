package com.hzy.blog.service.impl;

import com.hzy.blog.entity.ArticleType;
import com.hzy.blog.mapper.ArticleTypeMapper;
import com.hzy.blog.service.IArticleTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzy.blog.vo.ArticleTypeTreeVo;
import com.hzy.blog.vo.ArticleTypeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hzy
 * @since 2024-04-13
 */
@Service
public class ArticleTypeServiceImpl extends ServiceImpl<ArticleTypeMapper, ArticleType> implements IArticleTypeService {

    @Resource
    private ArticleTypeMapper articleTypeMapper;

    /**
     * 文章类型列表，包含文章数量
     * @return
     */
    @Override
    public List<ArticleTypeVo> articleTypeList() {
        return articleTypeMapper.articleTypeList();
    }

    /**
     * 获取首页文章类型树形目录
     * @param articleTypeParentId
     * @return
     */
    @Override
    public List<ArticleTypeTreeVo> getIndexArticleTypeList(String articleTypeParentId) {
        return articleTypeMapper.getIndexArticleTypeList(articleTypeParentId);
    }
}
