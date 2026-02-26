package com.fanchenyi.diet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanchenyi.diet.model.dto.NutritionGoalRequest;
import com.fanchenyi.diet.model.entity.NutritionGoal;

public interface NutritionGoalService extends IService<NutritionGoal> {

    /**
     * 设置或更新用户的营养目标
     * @param request 目标参数
     * @param userId 用户ID
     */
    void saveOrUpdateGoal(NutritionGoalRequest request, Long userId);

    /**
     * 获取用户的营养目标
     * @param userId 用户ID
     * @return 营养目标实体
     */
    NutritionGoal getUserGoal(Long userId);
}