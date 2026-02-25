package com.fanchenyi.diet.model.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 每日营养统计视图对象 (VO)
 * 用于返回给前端展示当天的总摄入量
 */
@Data
public class DailyNutritionVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当日总卡路里
     */
    private BigDecimal totalCalories = BigDecimal.ZERO;

    /**
     * 当日总蛋白质
     */
    private BigDecimal totalProtein = BigDecimal.ZERO;

    /**
     * 当日总脂肪
     */
    private BigDecimal totalFat = BigDecimal.ZERO;

    /**
     * 当日总碳水化合物
     */
    private BigDecimal totalCarbohydrate = BigDecimal.ZERO;

    /**
     * 记录日期
     */
    private String date;
}