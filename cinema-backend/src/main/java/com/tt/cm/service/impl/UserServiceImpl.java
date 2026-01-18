package com.tt.cm.service.impl;

import com.tt.cm.common.utils.JwtUtil;
import com.tt.cm.common.utils.SaltUtils;
import com.tt.cm.domain.LoginUser;
import com.tt.cm.domain.User;
import com.tt.cm.domain.vo.SysUserVo;
import com.tt.cm.mapper.UserMapper;
import com.tt.cm.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<User> findAllUsers(User user) {
        return userMapper.findAllUsers(user);
    }

    @Override
    public User findUserById(Long id) {
        return userMapper.findUserById(id);
    }

    @Override
    public User findUserByName(String userName) {
        return userMapper.findUserByName(userName);
    }

    /**
     * 处理注册逻辑
     *
     * @param user
     * @return
     */
    @Override
    public int addUser(User user) {
        if (!isUserNameUnique(user.getUserName(), -1L)) {
            throw new AuthenticationException("用户名重复");
        }
        //处理密码：md5 + salt + hash散列
        String salt = SaltUtils.getSalt(8);
        Md5Hash md5Hash = new Md5Hash(user.getPassword(), salt, 1024);

        user.setPassword(md5Hash.toHex());
        user.setSalt(salt);
        return userMapper.addUser(user);
    }

    @Override
    public int updateUser(User user) {
        if (!isUserNameUnique(user.getUserName(), user.getUserId())) {
            throw new AuthenticationException("用户名重复");
        }
        User originUser = userMapper.findUserById(user.getUserId());
        if (originUser == null) {
            throw new AuthenticationException("用户不存在");
        }

        if (!originUser.getPassword().equals(user.getPassword())) {
            //修改了密码
            //重新处理密码存储
            String salt = SaltUtils.getSalt(8);
            Md5Hash md5Hash = new Md5Hash(user.getPassword(), salt, 1024);

            user.setPassword(md5Hash.toHex());
            user.setSalt(salt);
        }
        return userMapper.updateUser(user);
    }

    @Override
    public int deleteUser(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += userMapper.deleteUser(id);
        }
        return rows;
    }

    @Override
    public LoginUser login(SysUserVo sysUserVo) {
        //登录，先查询用户信息
        User user = userMapper.findUserByName(sysUserVo.getUserName());
        if (user == null) {
            throw new AuthenticationException("用户名不存在");
        }

        //验证密码
        Md5Hash md5Hash = new Md5Hash(sysUserVo.getPassword(), user.getSalt(), 1024);
        if (!user.getPassword().equals(md5Hash.toHex())) {
            throw new AuthenticationException("用户名或密码错误");
        }

        //设置登录用户对象
        LoginUser loginUser = findLoginUser(sysUserVo);

        //颁发token
        String token = JwtUtil.sign(user.getUserName(), user.getPassword());
        loginUser.setToken(token);
        return loginUser;
    }


    @Override
    public LoginUser findLoginUser(SysUserVo sysUserVo) {
        return userMapper.findLoginUser(sysUserVo);
    }

    @Override
    public boolean isUserNameUnique(String userName, Long userId) {
        List<Long> userIds = userMapper.checkUserNameUnique(userName);
        for (Long id : userIds) {
            if (id.equals(userId)) {
                return true;
            }
        }
        return userIds.isEmpty();
    }
}
