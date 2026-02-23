package com.fanchenyi.diet.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 营养目标表
 * 对应表名：nutrition_goal
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("nutrition_goal")
public class NutritionGoal extends BaseEntity {

    /**
     * 用户id
     */
    private Long userId;

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