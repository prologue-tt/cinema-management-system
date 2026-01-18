package com.tt.cm.mapper;

import com.tt.cm.domain.SysResource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
/**系统资源菜单 持久层*/
@Mapper
public interface SysResourceMapper {

    List<SysResource> findAllResources();

    List<SysResource> findWithChildren();

    List<SysResource> findAllWithAllChildren();

    SysResource findResourceById(Long id);

    int addResource(SysResource sysResource);

    int updateResource(SysResource sysResource);

    int deleteResource(Long id);

}
