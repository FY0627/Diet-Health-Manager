package com.fanchenyi.diet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanchenyi.diet.mapper.NutritionGoalMapper;
import com.fanchenyi.diet.model.dto.NutritionGoalRequest;
import com.fanchenyi.diet.model.entity.NutritionGoal;
import com.fanchenyi.diet.service.NutritionGoalService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NutritionGoalServiceImpl extends ServiceImpl<NutritionGoalMapper, NutritionGoal> implements NutritionGoalService {

    @Override
    public void saveOrUpdateGoal(NutritionGoalRequest request, Long userId) {
        // 1. 参数基础校验
        if (request.getTargetCalories() == null ||
                request.getTargetProtein() == null ||
                request.getTargetCarbohydrate() == null ||
                request.getTargetFat() == null) {
            throw new RuntimeException("营养目标各项数值均不能为空");
        }

        // 2. 保证数值不能为负数
        if (request.getTargetCalories().compareTo(BigDecimal.ZERO) < 0 ||
                request.getTargetProtein().compareTo(BigDecimal.ZERO) < 0 ||
                request.getTargetCarbohydrate().compareTo(BigDecimal.ZERO) < 0 ||
                request.getTargetFat().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("营养目标数值不能为负数");
        }

        // 3. 查找是否已经存在目标记录
        QueryWrapper<NutritionGoal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        NutritionGoal existGoal = this.getOne(queryWrapper);

        if (existGoal != null) {
            // 更新现有记录
            existGoal.setTargetCalories(request.getTargetCalories());
            existGoal.setTargetProtein(request.getTargetProtein());
            existGoal.setTargetCarbohydrate(request.getTargetCarbohydrate());
            existGoal.setTargetFat(request.getTargetFat());
            this.updateById(existGoal);
        } else {
            // 新增记录
            NutritionGoal newGoal = new NutritionGoal();
            newGoal.setUserId(userId);
            newGoal.setTargetCalories(request.getTargetCalories());
            newGoal.setTargetProtein(request.getTargetProtein());
            newGoal.setTargetCarbohydrate(request.getTargetCarbohydrate());
            newGoal.setTargetFat(request.getTargetFat());
            this.save(newGoal);
        }
    }

    @Override
    public NutritionGoal getUserGoal(Long userId) {
        QueryWrapper<NutritionGoal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }
}