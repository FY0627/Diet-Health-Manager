package com.fanchenyi.diet.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求 DTO
 * 对应手册：数据传输对象：xxxDTO，xxx为业务领域相关的名称 [cite: 149]
 */
@Data
public class UserRegisterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String checkPassword;

    /**
     * 性别 (1:男, 2:女)
     */
    private Integer gender;
}