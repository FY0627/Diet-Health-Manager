package com.fanchenyi.diet.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HealthDataRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 身高 (cm)
     */
    private BigDecimal height;

    /**
     * 体重 (kg)
     */
    private BigDecimal weight;
}