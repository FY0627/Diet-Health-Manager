package com.fanchenyi.diet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanchenyi.diet.mapper.DietRecordMapper;
import com.fanchenyi.diet.mapper.FoodMapper;
import com.fanchenyi.diet.model.dto.DietRecordRequest;
import com.fanchenyi.diet.model.entity.DietRecord;
import com.fanchenyi.diet.model.entity.Food;
import com.fanchenyi.diet.service.DietRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fanchenyi.diet.model.vo.DailyNutritionVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DietRecordServiceImpl extends ServiceImpl<DietRecordMapper, DietRecord> implements DietRecordService {

    @Autowired
    private FoodMapper foodMapper;

    @Override
    public void addRecord(DietRecordRequest request, Long userId) {
        // 1. 基础校验
        if (request.getFoodId() == null || request.getQuantity() == null || request.getMealType() == null) {
            throw new RuntimeException("参数不完整");
        }

        // 2. 查询食物是否存在
        Food food = foodMapper.selectById(request.getFoodId());
        if (food == null) {
            throw new RuntimeException("该食物不存在");
        }

        // 3. 构建记录实体
        DietRecord record = new DietRecord();
        record.setUserId(userId);
        record.setFoodId(food.getId());

        // 记录当时的食物名称（快照）
        record.setFoodNameSnapshot(food.getFoodName());
        record.setMealType(request.getMealType());
        record.setQuantity(request.getQuantity());

        // 如果没有传日期，默认为今天
        if (request.getRecordDate() != null) {
            record.setRecordDate(request.getRecordDate());
        } else {
            record.setRecordDate(LocalDate.now());
        }

        // 4. 保存到数据库
        this.save(record);
    }

    @Override
    public List<DietRecord> listDailyRecords(Long userId, String date) {
        QueryWrapper<DietRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        if (date != null && !date.isEmpty()) {
            queryWrapper.eq("record_date", LocalDate.parse(date));
        } else {
            // 默认查当天
            queryWrapper.eq("record_date", LocalDate.now());
        }

        // 按餐别排序 (早餐 -> 加餐)
        queryWrapper.orderByAsc("meal_type");

        return this.list(queryWrapper);
    }

    @Override
    public DailyNutritionVO calculateDailyNutrition(Long userId, String date) {
        // 1. 获取当天的所有饮食记录 (复用刚才写的方法)
        List<DietRecord> records = this.listDailyRecords(userId, date);

        DailyNutritionVO vo = new DailyNutritionVO();
        vo.setDate(date != null ? date : LocalDate.now().toString());

        // 如果当天没有记录，直接返回 0 的 VO
        if (records == null || records.isEmpty()) {
            return vo;
        }

        // 2. 遍历记录，进行换算与累加
        for (DietRecord record : records) {
            // 获取食物基准数据 (每100g的营养)
            Food food = foodMapper.selectById(record.getFoodId());
            if (food == null) {
                continue; // 容错处理：如果食物丢了，跳过这条记录
            }

            // 计算比例：实际摄入量 / 100
            // 注意 divide 时要指定保留小数位和舍入模式（四舍五入），否则除不尽会报错
            BigDecimal ratio = record.getQuantity().divide(new BigDecimal("100"), 4, java.math.RoundingMode.HALF_UP);

            // 累加各项指标：原有值 = 原有值 + (食物每100g的值 * 比例)
            vo.setTotalCalories(vo.getTotalCalories().add(food.getCalories().multiply(ratio)));
            vo.setTotalProtein(vo.getTotalProtein().add(food.getProtein().multiply(ratio)));
            vo.setTotalFat(vo.getTotalFat().add(food.getFat().multiply(ratio)));
            vo.setTotalCarbohydrate(vo.getTotalCarbohydrate().add(food.getCarbohydrate().multiply(ratio)));
        }

        // 3. 最终结果保留两位小数 (四舍五入)
        vo.setTotalCalories(vo.getTotalCalories().setScale(2, java.math.RoundingMode.HALF_UP));
        vo.setTotalProtein(vo.getTotalProtein().setScale(2, java.math.RoundingMode.HALF_UP));
        vo.setTotalFat(vo.getTotalFat().setScale(2, java.math.RoundingMode.HALF_UP));
        vo.setTotalCarbohydrate(vo.getTotalCarbohydrate().setScale(2, java.math.RoundingMode.HALF_UP));

        return vo;
    }
}