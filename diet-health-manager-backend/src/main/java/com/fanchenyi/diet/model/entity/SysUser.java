package com.fanchenyi.diet.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 密码哈希值
     */
    private String passwordHash;

    /**
     * 出生日期
     * 对应数据库中date，使用LocalDate
     */
    private LocalDate birthday;

    /**
     * 性别(0: 未知, 1: 男, 2: 女)
     * 使用 Integer 包装类
     */
    private Integer gender;
}
