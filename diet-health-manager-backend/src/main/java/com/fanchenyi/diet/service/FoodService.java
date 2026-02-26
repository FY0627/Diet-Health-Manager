package com.fanchenyi.diet.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fanchenyi.diet.model.dto.FoodQueryRequest;
import com.fanchenyi.diet.model.entity.Food;

public interface FoodService extends IService<Food> {

    /**
     * 分页搜索食物库
     */
    Page<Food> pageSearchFood(FoodQueryRequest request);

    /**
     * 添加自定义食物
     */
    void addCustomFood(Food food);
}