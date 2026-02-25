package com.fanchenyi.diet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanchenyi.diet.model.dto.DietRecordRequest;
import com.fanchenyi.diet.model.entity.DietRecord;
import com.fanchenyi.diet.model.vo.DailyNutritionVO;

import java.util.List;

public interface DietRecordService extends IService<DietRecord> {

    /**
     * 添加饮食记录
     * @param request 记录参数
     * @param userId 当前用户ID
     */
    void addRecord(DietRecordRequest request, Long userId);

    /**
     * 获取用户某天的饮食记录
     * @param userId 用户ID
     * @param date 日期
     * @return 记录列表
     */
    List<DietRecord> listDailyRecords(Long userId, String date);

    /**
     * 计算用户某天的营养总摄入量
     * @param userId 用户ID
     * @param date 日期
     * @return 营养统计视图对象
     */
    DailyNutritionVO calculateDailyNutrition(Long userId, String date);
}