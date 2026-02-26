package com.fanchenyi.diet.model.dto;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class NutritionGoalRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 目标卡路里
     */
    private BigDecimal targetCalories;

    /**
     * 目标蛋白质
     */
    private BigDecimal targetProtein;

    /**
     * 目标碳水化合物
     */
    private BigDecimal targetCarbohydrate;

    /**
     * 目标脂肪
     */
    private BigDecimal targetFat;
}