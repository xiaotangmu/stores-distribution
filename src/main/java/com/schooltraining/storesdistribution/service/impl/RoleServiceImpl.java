package com.schooltraining.storesdistribution.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.schooltraining.storesdistribution.entities.Authority;
import com.schooltraining.storesdistribution.entities.Role;
import com.schooltraining.storesdistribution.mapper.AuthorityMapper;
import com.schooltraining.storesdistribution.mapper.RoleMapper;
import com.schooltraining.storesdistribution.service.RoleService;
import com.schooltraining.storesdistribution.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    AuthorityMapper authorityMapper;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public List<Role> getRoles(String roleName) {
        List<Role> roles = roleMapper.selectRoleLikeRoleName(roleName);
        if (roles != null && roles.size() > 0) {
            roles.forEach(role -> {
                List<Authority> authorityByRoleId = authorityMapper.getAuthorityByRoleId(role.getId());
                role.setAuthorities(authorityByRoleId);
            });
            return roles;
        }
        return null;
    }

    @Override
    public int update(Role role) {
        Jedis jedis = null;
        int i = roleMapper.updateByPrimaryKeySelective(role);
//        System.out.println(role);//会更新
        if(i > 0){
            updateRolesCache(role, 1, 0);
        }
        return i;
    }

    @Override
    public int delete(int id) {
        int i =  roleMapper.deleteByPrimaryKey(id);
        if(i > 0){
            updateRolesCache(null, 3, id);
        }
        return i;
    }

    @Override
    public Role add(Role role) {
        List<Authority> authorities = role.getAuthorities();
        int i = roleMapper.insert(role);
        if (i > 0) {
            if (authorities != null && authorities.size() > 0) {
                authorities.forEach(authority -> {
                    roleMapper.insertRoleWithAuthRelation(role.getId(), authority.getId());
                });
            }
        }

        //更新缓存
        if (i > 0) {
            updateRolesCache(role, 1, 0);
        }
        return role;
    }

    @Override
    public Map<String, String> getAll() {
//        Map<Integer, Role> roles = new HashMap<>();
        Map<String, String> roles2 = null;
        Jedis jedis = null;
        RLock lock = null;
        boolean tryLock = false;

        try {
            //从缓存中获取
            jedis = redisUtil.getJedis();
            roles2 = jedis.hgetAll("role:all:info");
            if (roles2 != null && roles2.size() > 0) {//缓存中有数据
                return roles2;
            }
            System.out.println(roles2);
            //同步到缓存
            // 设置分布式锁
            lock = redissonClient.getLock("role:all:lock");// 声明锁
            tryLock = lock.tryLock();
            if (tryLock) {//成功上锁
                //缓存中没有数据,从数据库中获取
                List<Role> list = roleMapper.selectAllRole();

                if (list != null && list.size() > 0) {
                    // mysql查询结果存入redis
                    Map<String, String> map = new HashMap<>();
                    list.forEach(role -> {
//                        roles.put(role.getId(), role);
                        map.put(role.getId() + "", JSON.toJSONString(role));
                    });
                    roles2 = map;
                    jedis.hmset("role:all:info", map);
                } else {
                    // 数据库没有
                    // 为了防止缓存穿透将，null或者空字符串值设置给redis
                    jedis.setex("role:all:info", 60 * 3, JSON.toJSONString(""));
                }
            } else {//有锁,自旋
                return getAll();
            }
            return roles2;
        } finally {
            if (tryLock) {
                lock.unlock();// 解锁
            }
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void updateRolesCache(Role role, int status, Integer deleteRoleId) {//status = 1 -- add/ update, 3 -- delete
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            Map<String, String> rolesStrMap = jedis.hgetAll("role:all:info");
            if (rolesStrMap != null && rolesStrMap.size() > 0) {//缓存中有
                if(status == 1){//添加/更新 -- 覆盖
                    rolesStrMap.put(role.getId() + "", JSON.toJSONString(role));
                    jedis.hmset("role:all:info", rolesStrMap);
                }else if(status == 3){//删除
                    jedis.hdel("role:all:info", deleteRoleId+"");//不能直接覆盖，还是会存在，要指定删除
                }
            }
            //缓存中没有不做处理
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }
}
