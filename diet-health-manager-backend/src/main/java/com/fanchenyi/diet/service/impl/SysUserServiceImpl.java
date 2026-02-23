package com.fanchenyi.diet.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanchenyi.diet.mapper.SysUserMapper;
import com.fanchenyi.diet.model.dto.UserRegisterRequest;
import com.fanchenyi.diet.model.entity.SysUser;
import com.fanchenyi.diet.service.SysUserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    // 盐值，混淆密码，防止被简单破解
    private static final String SALT = "diet_manager_2026";

    @Override
    public long register(UserRegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String checkPassword = request.getCheckPassword();
        Integer gender = request.getGender();

        // 1. 校验参数是否为空
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || !StringUtils.hasText(checkPassword)) {
            throw new RuntimeException("参数不能为空");
        }

        // 2. 校验密码长度（例如不小于6位）
        if (password.length() < 6) {
            throw new RuntimeException("密码长度不能少于6位");
        }

        // 3. 校验两次密码是否一致
        if (!password.equals(checkPassword)) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 4. 校验账户是否重复
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("账号已存在");
        }

        // 5. 密码加密 (MD5 + 盐)
        String encryptPassword = DigestUtil.md5Hex(SALT + password);

        // 6. 插入数据
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPasswordHash(encryptPassword);
        user.setGender(gender != null ? gender : 0); // 默认为未知

        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new RuntimeException("注册失败，数据库错误");
        }

        return user.getId();
    }

    @Override
    public SysUser login(String username, String password) {
        // 1. 校验
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new RuntimeException("账号或密码不能为空");
        }

        // 2. 密码加密（用同样的盐值加密后对比）
        String encryptPassword = DigestUtil.md5Hex(SALT + password);

        // 3. 查询用户
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password_hash", encryptPassword);
        SysUser user = this.getOne(queryWrapper);

        // 4. 判断用户是否存在
        if (user == null) {
            throw new RuntimeException("账号或密码错误"); // 模糊提示，增加安全性
        }

        // 5. 用户脱敏（安全规约：用户敏感数据禁止直接展示 [cite: 920]）
        SysUser safetyUser = new SysUser();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setGender(user.getGender());
        safetyUser.setBirthday(user.getBirthday());
        safetyUser.setCreateTime(user.getCreateTime());
        // 注意：这里绝对不返回 passwordHash

        return safetyUser;
    }
}