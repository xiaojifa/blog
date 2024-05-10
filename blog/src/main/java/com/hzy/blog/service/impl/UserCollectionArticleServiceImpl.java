package com.hzy.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzy.blog.entity.UserCollectionArticle;
import com.hzy.blog.mapper.UserCollectionArticleMapper;
import com.hzy.blog.service.IUserCollectionArticleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户收藏的文章 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2021-12-29
 */
@Service
public class UserCollectionArticleServiceImpl extends ServiceImpl<UserCollectionArticleMapper, UserCollectionArticle> implements IUserCollectionArticleService {

}
