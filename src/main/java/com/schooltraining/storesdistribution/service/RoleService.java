package com.schooltraining.storesdistribution.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.schooltraining.storesdistribution.entities.Role;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

public interface RoleService {

    public List<Role> getRoles(String roleName);

    public int update(Role role);

    public int delete(int id);

    public Role add(Role role);

    public Map<String, String> getAll();
}
