package com.fanchenyi.diet.service;

import com.fanchenyi.diet.model.vo.DailyAnalysisVO;

public interface AnalysisService {

    /**
     * 获取每日综合营养分析看板数据
     * @param userId 用户ID
     * @param date 日期
     * @return 综合分析 VO
     */
    DailyAnalysisVO getDailyAnalysis(Long userId, String date);
}