package com.hzy.blog.service;

import com.hzy.blog.entity.Ad;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hzy.blog.vo.AdVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzy
 * @since 2024-04-13
 */
public interface IAdService extends IService<Ad> {

    /**
     * 广告列表，包含类型名称
     *
     * @param adTypeId
     * @return
     */
    List<AdVo> adList(String adTypeId);
}
