package com.fanchenyi.diet.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 饮食记录表
 * 对应表名：diet_record
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("diet_record")
public class DietRecord extends BaseEntity {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 食物id
     */
    private Long foodId;

    /**
     * 食物名称快照
     */
    private String foodNameSnapshot;

    /**
     * 餐别(0:早餐,1:午餐,2:晚餐,3:加餐)
     */
    private Integer mealType;

    /**
     * 摄入量
     */
    private BigDecimal quantity;

    /**
     * 记录日期
     */
    private LocalDate recordDate;
}