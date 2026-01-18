package com.tt.cm.service;

import com.tt.cm.domain.SysResource;

import java.util.List;

/**
 * 系统资源服务层
 */
public interface SysResourceService {

    List<SysResource> findAllResources();

    List<SysResource> findWithChildren();

    List<SysResource> findAllWithAllChildren();

    SysResource findResourceById(Long id);

    int addResource(SysResource sysResource);

    int updateResource(SysResource sysResource);

    int deleteResource(Long[] ids);
}
