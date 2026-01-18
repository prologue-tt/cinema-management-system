package com.tt.cm.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装用户登录输入的信息
 */
@Data
public class SysUserVo implements Serializable {

    private String userName;

    private String password;
}
