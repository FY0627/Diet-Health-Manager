package com.fanchenyi.diet.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 饮食记录请求参数 DTO
 */
@Data
public class DietRecordRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 食物ID: foodId (必填)
     */
    private Long foodId;

    /**
     * 餐别: mealType (0: Breakfast, 1: Lunch, 2: Dinner, 3: Others) (必填)
     */
    private Integer mealType;

    /**
     * 摄入量: quantity (单位：克/毫升) (必填)
     */
    private BigDecimal quantity;

    /**
     * 记录日期: recordDate (如果不传，默认当天)
     */
    private LocalDate recordDate;
}
