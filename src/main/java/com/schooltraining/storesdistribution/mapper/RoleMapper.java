package com.schooltraining.storesdistribution.mapper;

import com.schooltraining.storesdistribution.entities.Role;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RoleMapper extends Mapper<Role>{

    public int insertRoleWithAuthRelation(@Param("roleId")int roleId, @Param("authorityId") int authorityId);

    public List<Role> selectRoleLikeRoleName(String roleName);

    public List<Role> selectAllRole();

    public List<Role> selectNotAuthorityRole();

    int deleteRelationWithAuthority(@Param("roleId") Integer roleId, @Param("authIds") List<Integer> authIds);

    int insertRelations(@Param("roleId") Integer roleId, @Param("authIds") List<Integer> authIds);

    int deleteRelations(@Param("roleId") Integer roleId);
}
