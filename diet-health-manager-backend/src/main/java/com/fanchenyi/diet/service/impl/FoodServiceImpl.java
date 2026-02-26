package com.fanchenyi.diet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanchenyi.diet.mapper.FoodMapper;
import com.fanchenyi.diet.model.dto.FoodQueryRequest;
import com.fanchenyi.diet.model.entity.Food;
import com.fanchenyi.diet.service.FoodService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food> implements FoodService {

    @Override
    public Page<Food> pageSearchFood(FoodQueryRequest request) {
        // 1. 构建分页对象 (当前页，每页条数)
        Page<Food> page = new Page<>(request.getCurrent(), request.getSize());

        // 2. 构建查询条件
        QueryWrapper<Food> queryWrapper = new QueryWrapper<>();

        // 如果传了关键字，则使用 LIKE 模糊查询
        if (StringUtils.hasText(request.getKeyword())) {
            queryWrapper.like("food_name", request.getKeyword());
        }

        // 按创建时间倒序排列，新加的食物在前面
        queryWrapper.orderByDesc("create_time");

        // 3. 执行分页查询
        return this.page(page, queryWrapper);
    }

    @Override
    public void addCustomFood(Food food) {
        // 基础校验
        if (!StringUtils.hasText(food.getFoodName()) || food.getCalories() == null) {
            throw new RuntimeException("食物名称和卡路里不能为空");
        }

        // 校验重名
        QueryWrapper<Food> wrapper = new QueryWrapper<>();
        wrapper.eq("food_name", food.getFoodName());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("该食物名称已存在");
        }

        this.save(food);
    }
}