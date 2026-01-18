package com.tt.cm.controller;

import com.tt.cm.common.response.R;
import com.tt.cm.domain.User;
import com.tt.cm.domain.vo.SysUserVo;
import com.tt.cm.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    /**
     * 查询用户 带查询条件
     *
     * @param user 用户信息
     * @return 用户列表
     */
    @GetMapping("/sysUser")
    public R findAllUsers(User user) {
        startPage();
        List<User> data = userService.findAllUsers(user);
        return getResult(data);
    }

    /**
     * 通过id查询用户
     *
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/sysUser/{id}")
    public R findUserById(@PathVariable Long id) {
        return getResult(userService.findUserById(id));
    }

    /**
     * 添加用户请求，注册也在这里
     *
     * @param user 用户信息
     * @return 结果
     */
    @PostMapping("/sysUser")
    public R addUser(@Validated @RequestBody User user) {
        return getResult(userService.addUser(user));
    }

    /**
     * 更新用户
     *
     * @param user 用户信息
     * @return 结果
     */
    @PutMapping("/sysUser")
    public R updateUser(@Validated @RequestBody User user) {
        return getResult(userService.updateUser(user));
    }

    /**
     * 删除用户
     *
     * @param ids 用户id
     * @return 结果
     */
    @DeleteMapping("/sysUser/{ids}")
    public R deleteUser(@PathVariable Long[] ids) {
        return getResult(userService.deleteUser(ids));
    }

    /**
     * 用户登录请求
     *
     * @param sysUserVo 登录信息
     * @return 登录结果
     */
    @RequestMapping("/user/login")
    public R login(@RequestBody SysUserVo sysUserVo) {
        return getResult(userService.login(sysUserVo));
    }

    /**
     * 用户注册请求
     *
     * @param user 用户信息
     * @return 结果
     */
    @PostMapping("/sysUser/register")
    public R register(@Validated @RequestBody User user) {
        // 注册只接收部分参数值，重新建立一个实例对象只接受注册接受的参数
        User registerUserInfo = new User();
        registerUserInfo.setUserName(user.getUserName());
        registerUserInfo.setPassword(user.getPassword());
        registerUserInfo.setSex(user.getSex());
        registerUserInfo.setPhoneNumber(user.getPhoneNumber());
        return getResult(userService.addUser(registerUserInfo));
    }

}
