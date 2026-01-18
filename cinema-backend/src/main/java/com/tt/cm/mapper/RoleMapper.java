package com.tt.cm.mapper;

import com.tt.cm.domain.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface RoleMapper {

    List<Role> findAllRoles();

    Role findRoleById(Long id);

    int addRole(Role role);

    int updateRole(Role role);

    int deleteRole(Long id);

    int addRight(Long roleId, Long resourceId);

    int deleteRight(Long roleId, Long resourceId);

    List<Long> findAllRights(Long roleId);

}
