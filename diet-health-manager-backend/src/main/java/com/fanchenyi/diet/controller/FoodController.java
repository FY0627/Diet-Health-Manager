package com.fanchenyi.diet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanchenyi.diet.common.Result;
import com.fanchenyi.diet.model.dto.FoodQueryRequest;
import com.fanchenyi.diet.model.entity.Food;
import com.fanchenyi.diet.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    /**
     * 分页查询食物
     */
    @PostMapping("/list")
    public Result<Page<Food>> listFood(@RequestBody FoodQueryRequest request) {
        Page<Food> pageResult = foodService.pageSearchFood(request);
        return Result.success(pageResult);
    }

    /**
     * 添加自定义食物
     */
    @PostMapping("/add")
    public Result<String> addFood(@RequestBody Food food) {

        foodService.addCustomFood(food);
        return Result.success("添加食物成功");

    }
}