package com.fanchenyi.diet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fanchenyi.diet.model.entity.DietRecord;
import com.fanchenyi.diet.model.entity.Food;
import com.fanchenyi.diet.model.entity.HealthData;
import com.fanchenyi.diet.model.entity.NutritionGoal;
import com.fanchenyi.diet.model.vo.DailyAnalysisVO;
import com.fanchenyi.diet.model.vo.DailyNutritionVO;
import com.fanchenyi.diet.service.AnalysisService;
import com.fanchenyi.diet.service.DietRecordService;
import com.fanchenyi.diet.service.FoodService;
import com.fanchenyi.diet.service.HealthDataService;
import com.fanchenyi.diet.service.NutritionGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private DietRecordService dietRecordService;

    @Autowired
    private NutritionGoalService nutritionGoalService;

    @Autowired
    private HealthDataService healthDataService;

    // ✅ 注入 FoodService，用于查询食物具体热量来计算每餐摄入
    @Autowired
    private FoodService foodService;

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

        // 3. 获取基础代谢和 BMI 数据 (如果有的话)
        QueryWrapper<HealthData> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        HealthData healthData = healthDataService.getOne(wrapper);
        if (healthData != null) {
            analysisVO.setBmr(healthData.getBmr());
            analysisVO.setBmi(healthData.getBmi()); // ✅ 补充展示 BMI
        } else {
            analysisVO.setBmr(BigDecimal.ZERO);
            analysisVO.setBmi(BigDecimal.ZERO);
        }

        // 4. 数据拼装与计算 (总目标与总摄入)
        analysisVO.setTargetCalories(goal.getTargetCalories());
        analysisVO.setTargetProtein(goal.getTargetProtein());
        analysisVO.setTargetCarbohydrate(goal.getTargetCarbohydrate());
        analysisVO.setTargetFat(goal.getTargetFat());

        analysisVO.setActualCalories(intake.getTotalCalories());
        analysisVO.setActualProtein(intake.getTotalProtein());
        analysisVO.setActualCarbohydrate(intake.getTotalCarbohydrate());
        analysisVO.setActualFat(intake.getTotalFat());

        // 计算剩余卡路里: 目标 - 实际
        BigDecimal remainingCal = goal.getTargetCalories().subtract(intake.getTotalCalories());
        analysisVO.setRemainingCalories(remainingCal);

        // 5. 按餐别统计热量 (0:早餐, 1:午餐, 2:晚餐, 3:加餐)
        List<DietRecord> dailyRecords = dietRecordService.listDailyRecords(userId, targetDate);
        for (DietRecord record : dailyRecords) {
            Food food = foodService.getById(record.getFoodId());
            if (food != null) {
                // 计算比例：实际摄入量 / 100
                BigDecimal ratio = record.getQuantity().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
                // 当前记录的热量
                BigDecimal recordCal = food.getCalories().multiply(ratio).setScale(2, RoundingMode.HALF_UP);

                // 根据餐别累加到 VO 中
                if (record.getMealType() == 0) {
                    analysisVO.setBreakfastCalories(analysisVO.getBreakfastCalories().add(recordCal));
                } else if (record.getMealType() == 1) {
                    analysisVO.setLunchCalories(analysisVO.getLunchCalories().add(recordCal));
                } else if (record.getMealType() == 2) {
                    analysisVO.setDinnerCalories(analysisVO.getDinnerCalories().add(recordCal));
                } else if (record.getMealType() == 3) {
                    analysisVO.setSnackCalories(analysisVO.getSnackCalories().add(recordCal));
                }
            }
        }

        // 6. 简单的智能建议生成
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