package com.fanchenyi.diet.controller;

import com.fanchenyi.diet.common.Result;
import com.fanchenyi.diet.model.dto.UserLoginRequest;
import com.fanchenyi.diet.model.dto.UserRegisterRequest;
import com.fanchenyi.diet.model.entity.SysUser;
import com.fanchenyi.diet.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    public Result<Long> register(@RequestBody UserRegisterRequest request) {
        if (request == null) {
            return Result.error("请求参数为空");
        }
        try {
            long result = sysUserService.register(request);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody UserLoginRequest request, HttpServletRequest httpServletRequest) {
        if (request == null) {
            return Result.error("请求参数为空");
        }
        try {
            SysUser user = sysUserService.login(request.getUsername(), request.getPassword());
            // 登录成功后，将用户信息存入 Session (简化版登录状态管理)
            httpServletRequest.getSession().setAttribute("user_login", user);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/current")
    public Result<SysUser> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute("user_login");
        SysUser currentUser = (SysUser) userObj;
        if (currentUser == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(currentUser);
    }
}