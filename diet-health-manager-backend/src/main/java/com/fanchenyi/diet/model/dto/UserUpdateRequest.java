package com.fanchenyi.diet.model.dto;

import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UserUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 新密码 (如果不传就不修改)
     */
    private String password;

    /**
     * 出生日期 (格式：yyyy-MM-dd)
     */
    private LocalDate birthday;

    /**
     * 性别 (1:男, 2:女)
     */
    private Integer gender;
}