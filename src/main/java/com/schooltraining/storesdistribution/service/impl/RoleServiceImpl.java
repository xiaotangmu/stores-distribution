package com.schooltraining.storesdistribution.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.schooltraining.storesdistribution.entities.Authority;
import com.schooltraining.storesdistribution.entities.Role;
import com.schooltraining.storesdistribution.mapper.AuthorityMapper;
import com.schooltraining.storesdistribution.mapper.RoleMapper;
import com.schooltraining.storesdistribution.service.AuthorityService;
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
    AuthorityService authorityService;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;
    
    private final static String ROLES_CACHE_KEY = "role:all:info";//缓存所有role的key名
    
    @Override
    public Role getRoleById(int roleId){
    	Jedis jedis = null;
    	try {
            //查找缓存
    		jedis = redisUtil.getJedis();
    		//查询role全部信息的缓存
    		String jsonStrRole = jedis.hget(ROLES_CACHE_KEY, roleId + "");
    		if(StringUtils.isBlank(jsonStrRole)){
                //查询特定role 的缓存
                jsonStrRole = jedis.get("role:" + roleId + ":info");
                if(StringUtils.isBlank(jsonStrRole)){
                    //缓存中没有，查询DB，有可能人工删缓存了
                    Role role = roleMapper.selectByPrimaryKey(roleId);
                    List<Authority> aList = authorityService.getAuthorityByRoleId(roleId);
                    role.setAuthorities(aList);
                    //保存缓存
                    jedis.setex("role:" + role.getId() + ":info", 60*60*3, JSON.toJSONString(role));
                    return role;
                }
            }
    		return JSON.parseObject(jsonStrRole, Role.class);
    	}finally {
    		if(jedis != null) {
    			jedis.close();
    		}
    	}
    }

    @Override
    public List<Role> getRoles(String roleName) {
        List<Role> roles = roleMapper.selectRoleLikeRoleName(roleName);
        if (roles != null && roles.size() > 0) {
            roles.forEach(role -> {
                List<Authority> authorityByRoleId = authorityService.getAuthorityByRoleId(role.getId());
                role.setAuthorities(authorityByRoleId);
            });
            return roles;
        }
        return null;
    }

    @Override
    public int update(Role role, List<Integer> ids) {
        Jedis jedis = null;
        int i = roleMapper.updateByPrimaryKeySelective(role);
        //更新关系
        roleMapper.deleteRelations(role.getId());
        roleMapper.insertRelations(role.getId(), ids);
//        System.out.println(ids);//会更新
        if(i != 0){
            updateRolesCache(role, 2, 0, ids);
        }
        return i;
    }

    @Override
    public int delete(int id) {
        int i =  roleMapper.deleteByPrimaryKey(id);
        //删除关系
        roleMapper.deleteRelations(id);
        if(i != 0){
            updateRolesCache(null, 3, id, null);
        }
        return i;
    }

    @Override
    public Role add(Role role, List<Integer> authorityIds) {
        List<Authority> authorities = role.getAuthorities();
        int i = roleMapper.insert(role);
        if (i != 0) {
//            System.out.println(i);
            if (authorityIds != null && authorityIds.size() > 0) {
                authorityIds.forEach(id -> {
//            if (authorities != null && authorities.size() > 0) {
//                authorities.forEach(authority -> {
                    roleMapper.insertRoleWithAuthRelation(role.getId(), id);
//                    roleMapper.insertRoleWithAuthRelation(role.getId(), authority.getId());
                });
            }
//            System.out.println(role);
            //更新缓存
            updateRolesCache(role, 1, 0, authorityIds);
        }
//        System.out.println(role);
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
            roles2 = jedis.hgetAll(ROLES_CACHE_KEY);
            if (roles2 != null && roles2.size() > 0) {//缓存中有数据
                return roles2;
            }
//            System.out.println(roles2);
            //同步到缓存
            // 设置分布式锁
            lock = redissonClient.getLock("role:all:lock");// 声明锁
            tryLock = lock.tryLock();
            if (tryLock) {//成功上锁
                //缓存中没有数据,从数据库中获取
                List<Role> list = roleMapper.selectAllRole();
                List<Role> rolesNoAuth = roleMapper.selectNotAuthorityRole();
                rolesNoAuth.forEach(role -> {
                    role.setAuthorities(new LinkedList<>());
                    list.add(role);
                });
//                System.out.println(list);
                if (list != null && list.size() > 0) {
                    // mysql查询结果存入redis
                    Map<String, String> map = new HashMap<>();
                    list.forEach(role -> {
//                        roles.put(role.getId(), role);
                        map.put(role.getId() + "", JSON.toJSONString(role));
                    });
                    roles2 = map;
                    jedis.hmset(ROLES_CACHE_KEY, map);
                } else {
                    // 数据库没有
                    // 为了防止缓存穿透将，null或者空字符串值设置给redis
                    jedis.setex(ROLES_CACHE_KEY, 60 * 3, JSON.toJSONString(""));
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

    public void updateRolesCache(Role role, int status, Integer deleteRoleId, List<Integer> ids) {//status = 1 -- add/ update, 3 -- delete
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            Map<String, String> rolesStrMap = jedis.hgetAll(ROLES_CACHE_KEY);
//            System.out.println(rolesStrMap);
            if (rolesStrMap != null && rolesStrMap.size() > 0) {//缓存中有
                if(status == 1 || status == 2){//添加/更新 -- 覆盖
                    if(ids != null){
//                        System.out.println(role.getId());
                        List<Authority> authorityByRoleId = authorityService.getAuthorityByRoleId(role.getId());
//                        System.out.println(authorityByRoleId);
                        if(authorityByRoleId == null){
                            authorityByRoleId = new ArrayList<>();
//                            System.out.println("hello");
                        }
                        role.setAuthorities(authorityByRoleId);
//                        System.out.println(role);
                    }
                    rolesStrMap.put(role.getId() + "", JSON.toJSONString(role));
                    jedis.hmset(ROLES_CACHE_KEY, rolesStrMap);
                }else if(status == 3){//删除
                    jedis.hdel(ROLES_CACHE_KEY, deleteRoleId+"");//不能直接覆盖，还是会存在，要指定删除
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
