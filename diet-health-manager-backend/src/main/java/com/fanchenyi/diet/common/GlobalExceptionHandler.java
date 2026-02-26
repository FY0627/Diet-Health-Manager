package com.fanchenyi.diet.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 拦截所有 Controller 层抛出的异常，统一封装返回格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 拦截我们在 Service 层手动抛出的 RuntimeException (业务异常)
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        // 只打印警告日志，记录错误信息
        log.warn("业务线异常: {}", e.getMessage());
        // 将我们在 throw new RuntimeException("...") 里的提示语，原封不动地返回给前端
        return Result.error(e.getMessage());
    }

    /**
     * 拦截其他未知的系统异常 (如 NullPointerException, 数据库连接失败等)
     * 兜底处理，防止把大段的 Java 报错直接暴露给前端
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        // 打印极其严重的错误日志（包含完整堆栈信息），方便后端排查
        log.error("系统内部未捕获异常: ", e);
        // 对前端进行模糊提示，保障系统安全
        return Result.error(500, "系统内部繁忙，请稍后再试");
    }
}