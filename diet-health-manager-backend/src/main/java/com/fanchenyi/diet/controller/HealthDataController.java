package com.fanchenyi.diet.controller;

import com.fanchenyi.diet.common.Result;
import com.fanchenyi.diet.model.dto.HealthDataRequest;
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
            return Result.error(401, "请先登录");
        }

        try {
            healthDataService.saveOrUpdateHealthData(request, currentUser.getId());
            return Result.success("健康数据更新成功，BMI和BMR已自动计算");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}