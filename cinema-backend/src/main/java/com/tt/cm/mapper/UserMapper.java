package com.tt.cm.mapper;

import com.tt.cm.domain.LoginUser;
import com.tt.cm.domain.User;
import com.tt.cm.domain.vo.SysUserVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户相关 持久层

 */
@Mapper
public interface UserMapper {

    List<User> findAllUsers(User user);

    User findUserById(Long id);

    User findUserByName(String userName);

    int addUser(User user);

    int updateUser(User user);

    int deleteUser(Long id);

    LoginUser findLoginUser(SysUserVo sysUserVo);

    List<Long> checkUserNameUnique(String userName);
}
