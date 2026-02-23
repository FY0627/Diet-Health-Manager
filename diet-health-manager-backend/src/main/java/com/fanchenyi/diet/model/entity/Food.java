package com.fanchenyi.diet.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 食物表
 * 对应表名：food
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("food")
public class Food extends BaseEntity {

    /**
     * 食物名称
     */
    private String foodName;

    /**
     * 卡路里
     * 小数类型用 BigDecimal
     */
    private BigDecimal calories;

    /**
     * 蛋白质
     */
    private BigDecimal protein;

    /**
     * 脂肪
     */
    private BigDecimal fat;

    /**
     * 碳水化合物
     */
    private BigDecimal carbohydrate;
}
