package com.fanchenyi.diet.controller;

import com.fanchenyi.diet.common.Result;
import com.fanchenyi.diet.model.dto.DietRecordRequest;
import com.fanchenyi.diet.model.entity.DietRecord;
import com.fanchenyi.diet.model.entity.SysUser;
import com.fanchenyi.diet.model.vo.DailyNutritionVO;
import com.fanchenyi.diet.service.DietRecordService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diet")
public class DietRecordController {

    @Autowired
    private DietRecordService dietRecordService;

    /**
     * 添加饮食记录
     */
    @PostMapping("/add")
    public Result<String> addRecord(@RequestBody DietRecordRequest request, HttpServletRequest httpServletRequest) {
        // 从 Session 获取当前登录用户
        SysUser currentUser = (SysUser) httpServletRequest.getSession().getAttribute("user_login");
        if (currentUser == null) {
            return Result.error(401, "请先登录");
        }

        try {
            dietRecordService.addRecord(request, currentUser.getId());
            return Result.success("记录添加成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询某天的饮食记录
     * 使用 GET 请求，参数直接拼在 URL 后面，例如：/diet/daily?date=2026-02-23
     */
    @GetMapping("/daily")
    public Result<List<DietRecord>> getDailyRecords(@RequestParam(required = false) String date, HttpServletRequest httpServletRequest) {
        SysUser currentUser = (SysUser) httpServletRequest.getSession().getAttribute("user_login");
        if (currentUser == null) {
            return Result.error(401, "请先登录");
        }

        List<DietRecord> records = dietRecordService.listDailyRecords(currentUser.getId(), date);
        return Result.success(records);
    }

    /**
     * 获取某天营养总摄入量统计
     */
    @GetMapping("/nutrition-stats")
    public Result<DailyNutritionVO> getDailyNutritionStats(@RequestParam(required = false) String date, HttpServletRequest httpServletRequest) {
        SysUser currentUser = (SysUser) httpServletRequest.getSession().getAttribute("user_login");
        if (currentUser == null) {
            return Result.error(401, "请先登录");
        }

        DailyNutritionVO stats = dietRecordService.calculateDailyNutrition(currentUser.getId(), date);
        return Result.success(stats);
    }
}