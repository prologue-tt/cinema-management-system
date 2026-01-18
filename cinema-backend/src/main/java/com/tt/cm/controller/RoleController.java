package com.tt.cm.controller;

import com.tt.cm.common.response.R;
import com.tt.cm.domain.Role;
import com.tt.cm.service.RoleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统角色控制层
 */
@RestController
public class RoleController extends BaseController {

    @Resource
    private RoleService roleService;

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @GetMapping("/sysRole")
    public R findAllRoles() {
        startPage();
        List<Role> data = roleService.findAllRoles();
        return getResult(data);
    }

    /**
     * 通过id查询角色
     *
     * @param id 角色id
     * @return 角色
     */
    @GetMapping("/sysRole/{id}")
    public R findRoleById(@PathVariable Long id) {
        return getResult(roleService.findRoleById(id));
    }

    /**
     * 添加角色
     *
     * @param role 角色信息
     * @return 结果
     */
    @PostMapping("/sysRole")
    public R addRole(@Validated @RequestBody Role role) {
        return getResult(roleService.addRole(role));
    }

    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 结果
     */
    @PutMapping("/sysRole")
    public R updateRole(@Validated @RequestBody Role role) {
        return getResult(roleService.updateRole(role));
    }

    /**
     * 删除角色
     *
     * @param ids 角色id
     * @return 结果
     */
    @DeleteMapping("/sysRole/{ids}")
    public R deleteRole(@PathVariable Long[] ids) {
        return getResult(roleService.deleteRole(ids));
    }

    /**
     * 给指定 id 的角色分配权限，包括增加或者删除权限
     *
     * @param roleId 角色id
     * @param keys   权限key
     * @return 结果
     */
    @PostMapping("/sysRole/{roleId}")
    public R allotRight(@PathVariable Long roleId, @RequestBody Long[] keys) {
        return getResult(roleService.allotRight(roleId, keys));
    }

}
