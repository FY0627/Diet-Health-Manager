package com.fanchenyi.diet.model.vo;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 每日综合分析看板 VO
 */
@Data
public class DailyAnalysisVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String date;

    // === 基础参考 ===
    private BigDecimal bmr; // 基础代谢率
    private BigDecimal bmi; // BMI 体质指数

    // === 新增：按餐别统计的热量 ===
    private BigDecimal breakfastCalories = BigDecimal.ZERO;
    private BigDecimal lunchCalories = BigDecimal.ZERO;
    private BigDecimal dinnerCalories = BigDecimal.ZERO;
    private BigDecimal snackCalories = BigDecimal.ZERO;

    // === 卡路里分析 ===
    private BigDecimal targetCalories;
    private BigDecimal actualCalories;
    private BigDecimal remainingCalories; // 剩余可摄入卡路里 (如果超标则为负数)

    // === 蛋白质分析 ===
    private BigDecimal targetProtein;
    private BigDecimal actualProtein;

    // === 碳水分析 ===
    private BigDecimal targetCarbohydrate;
    private BigDecimal actualCarbohydrate;

    // === 脂肪分析 ===
    private BigDecimal targetFat;
    private BigDecimal actualFat;

    // === 智能建议 ===
    private String advice;


}