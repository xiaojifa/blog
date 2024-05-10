package com.hzy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzy.blog.entity.ArticleType;
import com.hzy.blog.vo.ArticleTypeTreeVo;
import com.hzy.blog.vo.ArticleTypeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzy
 * @since 2024-04-13
 */
public interface ArticleTypeMapper extends BaseMapper<ArticleType> {

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
    List<ArticleTypeTreeVo> getIndexArticleTypeList(@Param("articleTypeParentId") String articleTypeParentId);
}
