package com.fanchenyi.diet.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 健康数据表
 * 对应表名：health_data
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("health_data")
public class HealthData extends BaseEntity {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 身高(cm)
     */
    private BigDecimal height;

    /**
     * 体重(kg)
     */
    private BigDecimal weight;

    /**
     * BMI
     */
    private BigDecimal bmi;

    /**
     * 基础代谢率
     */
    private BigDecimal bmr;
}