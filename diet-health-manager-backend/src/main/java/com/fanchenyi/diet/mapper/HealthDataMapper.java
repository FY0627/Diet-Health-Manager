package com.fanchenyi.diet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanchenyi.diet.model.entity.HealthData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康数据表 Mapper 接口
 * 继承 BaseMapper 后，自动会 CURD
 */
@Mapper
public interface HealthDataMapper extends BaseMapper<HealthData> {
}
