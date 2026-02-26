package com.fanchenyi.diet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fanchenyi.diet.model.entity.HealthData;
import com.fanchenyi.diet.model.entity.NutritionGoal;
import com.fanchenyi.diet.model.vo.DailyAnalysisVO;
import com.fanchenyi.diet.model.vo.DailyNutritionVO;
import com.fanchenyi.diet.service.AnalysisService;
import com.fanchenyi.diet.service.DietRecordService;
import com.fanchenyi.diet.service.HealthDataService;
import com.fanchenyi.diet.service.NutritionGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private DietRecordService dietRecordService;

    @Autowired
    private NutritionGoalService nutritionGoalService;

    @Autowired
    private HealthDataService healthDataService;

    @Override
    public DailyAnalysisVO getDailyAnalysis(Long userId, String date) {
        String targetDate = (date != null && !date.isEmpty()) ? date : LocalDate.now().toString();
        DailyAnalysisVO analysisVO = new DailyAnalysisVO();
        analysisVO.setDate(targetDate);

        // 1. 获取目标数据 (如果没设置，必须抛出异常提醒)
        NutritionGoal goal = nutritionGoalService.getUserGoal(userId);
        if (goal == null) {
            throw new RuntimeException("请先设置每日营养目标");
        }

        // 2. 获取实际摄入数据 (复用之前写好的统计逻辑)
        DailyNutritionVO intake = dietRecordService.calculateDailyNutrition(userId, targetDate);

        // 3. 获取基础代谢数据 (如果有的话)
        QueryWrapper<HealthData> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        HealthData healthData = healthDataService.getOne(wrapper);
        if (healthData != null) {
            analysisVO.setBmr(healthData.getBmr());
        } else {
            analysisVO.setBmr(BigDecimal.ZERO);
        }

        // 4. 数据拼装与计算
        // 目标值
        analysisVO.setTargetCalories(goal.getTargetCalories());
        analysisVO.setTargetProtein(goal.getTargetProtein());
        analysisVO.setTargetCarbohydrate(goal.getTargetCarbohydrate());
        analysisVO.setTargetFat(goal.getTargetFat());

        // 实际值
        analysisVO.setActualCalories(intake.getTotalCalories());
        analysisVO.setActualProtein(intake.getTotalProtein());
        analysisVO.setActualCarbohydrate(intake.getTotalCarbohydrate());
        analysisVO.setActualFat(intake.getTotalFat());

        // 计算剩余卡路里: 目标 - 实际
        BigDecimal remainingCal = goal.getTargetCalories().subtract(intake.getTotalCalories());
        analysisVO.setRemainingCalories(remainingCal);

        // 5. 简单的智能建议生成
        if (remainingCal.compareTo(BigDecimal.ZERO) < 0) {
            analysisVO.setAdvice("⚠️ 警告：您今天的热量摄入已超标 " + remainingCal.abs() + " 千卡！请注意控制。");
        } else if (remainingCal.compareTo(new BigDecimal("200")) < 0) {
            analysisVO.setAdvice("⚡ 提示：您今天剩余的热量额度已不足 200 千卡，即将达标。");
        } else {
            analysisVO.setAdvice("✅ 状态良好：今天还有 " + remainingCal + " 千卡的热量缺口，请合理安排饮食。");
        }

        return analysisVO;
    }
}