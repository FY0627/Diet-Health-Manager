package com.fanchenyi.diet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanchenyi.diet.mapper.HealthDataMapper;
import com.fanchenyi.diet.mapper.SysUserMapper;
import com.fanchenyi.diet.model.dto.HealthDataRequest;
import com.fanchenyi.diet.model.entity.HealthData;
import com.fanchenyi.diet.model.entity.SysUser;
import com.fanchenyi.diet.service.HealthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Service
public class HealthDataServiceImpl extends ServiceImpl<HealthDataMapper, HealthData> implements HealthDataService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public void saveOrUpdateHealthData(HealthDataRequest request, Long userId) {
        if (request.getHeight() == null || request.getWeight() == null) {
            throw new RuntimeException("身高和体重不能为空");
        }

        // 1. 查询用户信息（需要用到性别和生日来计算基础代谢）
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getBirthday() == null) {
            throw new RuntimeException("请先完善个人信息（性别和出生日期）");
        }

        // 2. 计算年龄
        int age = Period.between(user.getBirthday(), LocalDate.now()).getYears();

        // 3. 计算 BMI = weight(kg) / (height(m) * height(m))
        // 身高从 cm 转为 m
        BigDecimal heightInMeter = request.getHeight().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal bmi = request.getWeight().divide(heightInMeter.pow(2), 2, RoundingMode.HALF_UP);

        // 4. 计算 BMR (Mifflin-St Jeor 公式)
        // 基础公式: 10 * weight + 6.25 * height - 5 * age
        BigDecimal bmrBase = new BigDecimal("10").multiply(request.getWeight())
                .add(new BigDecimal("6.25").multiply(request.getHeight()))
                .subtract(new BigDecimal("5").multiply(new BigDecimal(age)));

        BigDecimal bmr;
        if (user.getGender() != null && user.getGender() == 2) {
            // 女性: bmrBase - 161
            bmr = bmrBase.subtract(new BigDecimal("161"));
        } else {
            // 男性: bmrBase + 5
            bmr = bmrBase.add(new BigDecimal("5"));
        }

        // 5. 查找是否已经有记录，如果有则更新，没有则新增
        QueryWrapper<HealthData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        HealthData existData = this.getOne(queryWrapper);

        if (existData != null) {
            existData.setHeight(request.getHeight());
            existData.setWeight(request.getWeight());
            existData.setBmi(bmi);
            existData.setBmr(bmr);
            this.updateById(existData);
        } else {
            HealthData newData = new HealthData();
            newData.setUserId(userId);
            newData.setHeight(request.getHeight());
            newData.setWeight(request.getWeight());
            newData.setBmi(bmi);
            newData.setBmr(bmr);
            this.save(newData);
        }
    }
}