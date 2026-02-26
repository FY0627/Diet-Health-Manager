package com.fanchenyi.diet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanchenyi.diet.model.dto.HealthDataRequest;
import com.fanchenyi.diet.model.entity.HealthData;

public interface HealthDataService extends IService<HealthData> {

    /**
     * 录入或更新健康数据（并自动计算 BMI 和 BMR）
     * @param request 身高体重数据
     * @param userId 当前用户ID
     */
    void saveOrUpdateHealthData(HealthDataRequest request, Long userId);
}