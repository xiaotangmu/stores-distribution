package com.schooltraining.storesdistribution.service;

import com.schooltraining.storesdistribution.entities.Authority;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AuthorityService{

    public List<Authority> getAll();

    public List<Authority> getAuthorityByRoleId(int roleId);
}
