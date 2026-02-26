package com.fanchenyi.diet.controller;

import com.fanchenyi.diet.common.Result;
import com.fanchenyi.diet.model.entity.SysUser;
import com.fanchenyi.diet.model.vo.DailyAnalysisVO;
import com.fanchenyi.diet.service.AnalysisService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    /**
     * 获取每日综合看板数据
     */
    @GetMapping("/daily-board")
    public Result<DailyAnalysisVO> getDailyBoard(@RequestParam(required = false) String date, HttpServletRequest httpServletRequest) {
        SysUser currentUser = (SysUser) httpServletRequest.getSession().getAttribute("user_login");
        if (currentUser == null) {
            return Result.error(401, "请先登录");
        }

        try {
            DailyAnalysisVO vo = analysisService.getDailyAnalysis(currentUser.getId(), date);
            return Result.success(vo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}