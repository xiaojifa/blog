package com.hzy.blog.mapper;

import com.hzy.blog.entity.Ad;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzy.blog.vo.AdVo;
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
public interface AdMapper extends BaseMapper<Ad> {

    /**
     * 广告列表，包含广告类型名称
     * @param adTypeId
     * @return
     */
    List<AdVo> adList(@Param("adTypeId") String adTypeId);
}
