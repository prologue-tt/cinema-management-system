package com.tt.cm.service;

import com.tt.cm.domain.Role;

import java.util.List;

/**
 * 系统角色服务层
 */
public interface RoleService {

    List<Role> findAllRoles();

    Role findRoleById(Long id);

    int addRole(Role role);

    int updateRole(Role role);

    int deleteRole(Long[] ids);

    int allotRight(Long roleId, Long[] resourceIds);
}
