package com.fanchenyi.diet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanchenyi.diet.model.dto.UserUpdateRequest;
import com.fanchenyi.diet.model.entity.SysUser;
import com.fanchenyi.diet.model.dto.UserRegisterRequest;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户注册
     *
     * @param request 注册参数
     * @return 新用户ID
     */
    long register(UserRegisterRequest request);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 脱敏后的用户信息
     */
    SysUser login(String username, String password);

    /**
     * 更新用户个人信息
     * @param request 更新参数
     * @param userId 当前用户ID
     */
    void updateUser(UserUpdateRequest request, Long userId);
}