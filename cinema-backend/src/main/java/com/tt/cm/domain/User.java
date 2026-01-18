package com.tt.cm.domain;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;
@Data
public class User implements Serializable {

    private final static Long serialVersionUID = 1L;

    //用户id
    private Long userId;
    //用户名
    @NotBlank(message = "用户名不能为空")
    private String userName;
    //密码
    @NotBlank(message = "密码不能为空")
    private String password;
    //盐
    private String salt;
    //邮箱
    @Email(message = "邮箱格式有误")
    private String email;
    //电话号码
    @Pattern(regexp = "^1[3|4|5|7|8]\\d{9}$", message = "电话号码格式有错")
    private String phoneNumber;
    //性别
    private Boolean sex;
    //用户头像
    private String userPicture;
    //用户对应的角色id，为简化操作，采用1对1
    private Long roleId;

    private String birthday;
    //签名
    private String autograph;

    //用户的角色
    private Role role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(userName, user.userName) && Objects.equals(password, user.password) && Objects.equals(salt, user.salt) && Objects.equals(email, user.email) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(sex, user.sex) && Objects.equals(userPicture, user.userPicture) && Objects.equals(roleId, user.roleId) && Objects.equals(birthday, user.birthday) && Objects.equals(autograph, user.autograph) && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, password, salt, email, phoneNumber, sex, userPicture, roleId, birthday, autograph, role);
    }

}
