package com.fanchenyi.diet.controller;

import com.fanchenyi.diet.common.Result;
import com.fanchenyi.diet.model.dto.NutritionGoalRequest;
import com.fanchenyi.diet.model.entity.NutritionGoal;
import com.fanchenyi.diet.model.entity.SysUser;
import com.fanchenyi.diet.service.NutritionGoalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goal")
public class NutritionGoalController {

    @Autowired
    private NutritionGoalService nutritionGoalService;

    /**
     * 设置或更新营养目标
     */
    @PostMapping("/update")
    public Result<String> updateGoal(@RequestBody NutritionGoalRequest request, HttpServletRequest httpServletRequest) {
        SysUser currentUser = (SysUser) httpServletRequest.getSession().getAttribute("user_login");
        if (currentUser == null) {
            return Result.error(401, "请先登录");
        }

        nutritionGoalService.saveOrUpdateGoal(request, currentUser.getId());
        return Result.success("营养目标设置成功");

    }

    /**
     * 获取当前用户的营养目标
     */
    @GetMapping("/info")
    public Result<NutritionGoal> getGoalInfo(HttpServletRequest httpServletRequest) {
        SysUser currentUser = (SysUser) httpServletRequest.getSession().getAttribute("user_login");
        if (currentUser == null) {
            return Result.error(401, "请先登录");
        }

        NutritionGoal goal = nutritionGoalService.getUserGoal(currentUser.getId());
        if (goal == null) {
            return Result.error("尚未设置营养目标");
        }
        return Result.success(goal);
    }
}