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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    /**
     * 删除饮食记录
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteRecord(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        SysUser currentUser = (SysUser) httpServletRequest.getSession().getAttribute("user_login");
        if (currentUser == null) {
            return Result.error(401, "请先登录");
        }

        // 安全校验：只能删除自己的记录
        DietRecord record = dietRecordService.getById(id);
        if (record == null) {
            return Result.error("记录不存在");
        }
        if (!record.getUserId().equals(currentUser.getId())) {
            return Result.error("无权删除他人的记录");
        }

        dietRecordService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 按用户分页查询饮食记录
     */
    @GetMapping("/page")
    public Result<Page<DietRecord>> pageRecords(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            HttpServletRequest httpServletRequest) {

        SysUser currentUser = (SysUser) httpServletRequest.getSession().getAttribute("user_login");
        if (currentUser == null) {
            return Result.error(401, "请先登录");
        }

        Page<DietRecord> page = new Page<>(current, size);
        QueryWrapper<DietRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUser.getId());
        // 按照记录日期倒序，新的记录在前面
        queryWrapper.orderByDesc("record_date", "create_time");

        Page<DietRecord> resultPage = dietRecordService.page(page, queryWrapper);
        return Result.success(resultPage);
    }
}