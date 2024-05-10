package com.hzy.blog.service.impl;

import com.hzy.blog.entity.Ad;
import com.hzy.blog.mapper.AdMapper;
import com.hzy.blog.service.IAdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzy.blog.vo.AdVo;
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
public class AdServiceImpl extends ServiceImpl<AdMapper, Ad> implements IAdService {

    @Resource
    private AdMapper adMapper;
    /**
     * 广告列表，包含类型名称
     *
     * @param adTypeId
     * @return
     */
    @Override
    public List<AdVo> adList(String adTypeId) {
        return adMapper.adList(adTypeId);
    }
}
