package com.fanchenyi.diet.model.dto;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@Data
public class FoodQueryRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 搜索关键字 (食物名称)
     */
    private String keyword;

    /**
     * 当前页码 (默认第一页)
     */
    private long current = 1;

    /**
     * 每页显示条数 (默认10条)
     */
    private long size = 10;
}