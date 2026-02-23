package com.fanchenyi.diet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanchenyi.diet.model.entity.DietRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 饮食记录表 Mapper 接口
 * 继承 BaseMapper 后，自动会 CURD
 */
@Mapper
public interface DietRecordMapper extends BaseMapper<DietRecord> {
}
