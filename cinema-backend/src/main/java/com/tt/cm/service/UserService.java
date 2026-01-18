package com.tt.cm.service;

import com.tt.cm.domain.LoginUser;
import com.tt.cm.domain.User;
import com.tt.cm.domain.vo.SysUserVo;

import java.util.List;


public interface UserService {

    List<User> findAllUsers(User user);

    User findUserById(Long id);

    User findUserByName(String userName);

    int addUser(User user);

    int updateUser(User user);

    int deleteUser(Long[] ids);

    LoginUser login(SysUserVo sysUserVo);

    LoginUser findLoginUser(SysUserVo sysUserVo);

    boolean isUserNameUnique(String userName, Long userId);
}
