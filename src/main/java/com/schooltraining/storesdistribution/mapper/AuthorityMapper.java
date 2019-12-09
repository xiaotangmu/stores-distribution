package com.schooltraining.storesdistribution.mapper;

import com.schooltraining.storesdistribution.entities.Authority;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AuthorityMapper extends Mapper<Authority>{

    List<Authority> getAuthorityByRoleId(int roleId);
}
