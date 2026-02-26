package com.fanchenyi.diet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fanchenyi.diet.common.Result;
import com.fanchenyi.diet.model.dto.HealthDataRequest;
import com.fanchenyi.diet.model.entity.HealthData;
import com.fanchenyi.diet.model.entity.SysUser;
import com.fanchenyi.diet.service.HealthDataService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/health")
public class HealthDataController {

    @Autowired
    private HealthDataService healthDataService;

    @PostMapping("/update")
    public Result<String> updateHealthData(@RequestBody HealthDataRequest request, HttpServletRequest httpServletRequest) {
        SysUser currentUser = (SysUser) httpServletRequest.getSession().getAttribute("user_login");
        if (currentUser == null) {
            throw new RuntimeException("请先登录");
        }
        healthDataService.saveOrUpdateHealthData(request, currentUser.getId());
        return Result.success("健康数据更新成功，BMI和BMR已自动计算");
    }

    /**
     * 新增：获取当前用户的身体指标（用于前端页面回显）
     */
    @GetMapping("/info")
    public Result<HealthData> getHealthInfo(HttpServletRequest httpServletRequest) {
        SysUser currentUser = (SysUser) httpServletRequest.getSession().getAttribute("user_login");
        if (currentUser == null) {
            throw new RuntimeException("请先登录");
        }

        QueryWrapper<HealthData> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentUser.getId());
        HealthData healthData = healthDataService.getOne(wrapper);

        return Result.success(healthData);
    }
}