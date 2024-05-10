package com.hzy.blog.service.impl;

import com.hzy.blog.entity.Link;
import com.hzy.blog.mapper.LinkMapper;
import com.hzy.blog.service.ILinkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hzy
 * @since 2024-04-13
 */
@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements ILinkService {

}
