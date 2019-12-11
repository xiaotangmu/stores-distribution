package com.schooltraining.storesdistribution.service.impl;

import com.alibaba.fastjson.JSON;
import com.schooltraining.storesdistribution.entities.Role;
import com.schooltraining.storesdistribution.entities.User;
import com.schooltraining.storesdistribution.mapper.UserMapper;
import com.schooltraining.storesdistribution.service.RoleService;
import com.schooltraining.storesdistribution.service.UserService;
import com.schooltraining.storesdistribution.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;
    
    @Autowired 
    RoleService roleService;

    @Override
    public User getUser(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    @Override
    public int setUser(User user) {
        return userMapper.insert(user);
    }

    @Override
    public User login(User user) {
        // 先查询redis
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();

            if (jedis != null) {
                // userName + passwork 作为中间唯一标记
                String umsMemberStr = jedis.get("user:" + user.getUserName() + user.getPassword() + ":info");

                if (StringUtils.isNotBlank(umsMemberStr)) {
                    // 密码正确
                    User umsMemberFromCache = JSON.parseObject(umsMemberStr, User.class);
                    return umsMemberFromCache;
                }
            }
            // 链接redis失败，查找数据库
            User umsMemberFromDb = getUserByUserNameAndPwd(user);

            if (umsMemberFromDb != null) {
                jedis.setex("user:" + umsMemberFromDb.getUserName() + umsMemberFromDb.getPassword() + ":info",
                        60 * 60 * 24, JSON.toJSONString(umsMemberFromDb));
            }
            return umsMemberFromDb;
        } finally {
            jedis.close();
        }

    }

    @Override
    public void addUserToken(String token, String userId) {
        Jedis jedis = redisUtil.getJedis();
        try {
            jedis.setex("user:" + userId + ":token", 60 * 60 * 2, token);//有效时间为2 个钟
        } finally {
            jedis.close();
        }
    }

    public User getUserByUserNameAndPwd(User user){

        List<User> selectByExample = userMapper.select(user);// 以非空字段作为查询条件
        if (selectByExample != null && selectByExample.size() > 0) {//没有不是null，是[]
			User user2 = selectByExample.get(0);
			user2.setRole(roleService.getRoleById(user2.getRoleId()));
            return user2;
        }
        return null;

    }

	@Override
	public List<User> getUserLikeName(String name) {
		if(StringUtils.isNotBlank(name)){
			List<User> users = userMapper.selectUserLikeName(name);
			if(users == null || users.get(0).getId() == null || users.get(0).getId() == 0){
				return null;
			}
			return users;
		}
		return null;
	}

	
	@Override
	public List<Integer> getRoleIdsByUserId(int userId) {
		List<Integer> roleIds = userMapper.getRoleIdsByUserId(userId);
		if(roleIds != null && roleIds.size() > 0) {
			return roleIds;
		}
		return null;
	}

	@Override
	public void setUserCache(User user) {
		Jedis jedis = null;
		try {
			jedis = redisUtil.getJedis();
			jedis.setex("user:" + user.getId() + ":info", 60*60*3, JSON.toJSONString(user));//缓存3个钟
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public User getUserInfo(int userId) {
		Jedis jedis = null;
		try {
			jedis = redisUtil.getJedis();
			//查询缓存
			String jsonStr = jedis.get("user:" + userId + ":info");
			if(StringUtils.isNotBlank(jsonStr)) {
				return JSON.parseObject(jsonStr, User.class);
			}
			//查询数据库
			User userFromDB = userMapper.selectByPrimaryKey(userId);
			if(userFromDB != null) {
				//通过用户id获取角色信息
//				List<Role> roles = getRolesByUserIds(userId);
//				userFromDB.setRoles(roles);
				
				Role userRole = roleService.getRoleById(userFromDB.getStoreId());
				userFromDB.setRole(userRole);
				
				//添加进缓存
				jedis.setex("user:" + userId + ":info", 60*60*48, JSON.toJSONString(userFromDB));
			}
			
			return userFromDB;
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
		
	}
	
	@Override
	public List<Role> getRolesByUserIds(int userId){
		//得到user对应的role 的 id 数组
		List<Integer> roleIds = getRoleIdsByUserId(userId);
		
//		System.out.println(roleIds);
		//得到角色信息
		List<Role> roles = new ArrayList<>();
		if(roleIds != null) {
			roleIds.forEach(rId -> {
				Role role = roleService.getRoleById(rId);
				roles.add(role);
			});
			if(roles.size() > 0) {
				return roles;
			}
		}
		return null;
	}

	
	@Override
	public int update(User user) {
		Jedis jedis = null;
		try {
			int uint = userMapper.updateByPrimaryKeySelective(user);
			if(uint != 0) {
				//更新缓存
				jedis = redisUtil.getJedis();
				String strKey = "user:" + user.getId() + ":info";
				String jsonStr = jedis.get(strKey);
				User user2 = JSON.parseObject(jsonStr, User.class);
//				user.setRoles(user2.getRoles());
				user.setRole(user2.getRole());
				jedis.set(strKey, JSON.toJSONString(user));
			}
			return uint;
		}finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}

	
	@Override
	public List<User> getAll() {
		List<User> users = userMapper.selectAll();
		List<User> users2 = new ArrayList<>();
		if(users != null && users.size() > 0) {
			users.forEach(user -> {
//				user.setRoles(getRolesByUserIds(user.getId()));
				user.setRole(roleService.getRoleById(user.getRoleId()));
//				System.out.println(user);
				users2.add(user);
			});
			return users2;
		}
		return null;
	}


	@Override
	public User addUser(User user) {
		Jedis jedis = null;
		try {
			user.setRoleId(1);//默认角色
			int i = userMapper.insertSelective(user);
			if(i != 0) {
				//加入缓存
				jedis = redisUtil.getJedis();
				jedis.setex("user:" + user.getUserName() + user.getPassword() + ":info", 60*60*2, JSON.toJSONString(user));
			}
			return user;
		}finally {
			if(jedis != null) {
				jedis.close();
			}
		}
		
	}

	@Override
	public int getUserByUserName(String userName) {//注册时判断是否已经存在该用户
		if(userName != null){
			Example example = new Example(User.class);
			Criteria createCriteria = example.createCriteria();
			createCriteria.andEqualTo("userName", userName);
			return userMapper.selectByExample(example).size();
		}
		return 0;
	}

	@Override
	public int updateRole(Integer id, Integer roleId) {
		Jedis jedis = null;
		try{
			if (id != null && roleId != null && id != 0 && roleId != 0){
				User user = new User();
				user.setId(id);
				user.setRoleId(roleId);
				int i = userMapper.updateByPrimaryKeySelective(user);
				if (i != 0) {
					//更新缓存 -- 删除缓存直接
					jedis = redisUtil.getJedis();
					String cacheKey = "user:" + id + ":info";
					String s = jedis.get(cacheKey);
					if (StringUtils.isBlank(s)){
						jedis.del(cacheKey);
					}
					return i;
				}
			}
			return 0;
		}finally{
			if (jedis != null) {
				jedis.close();
			}
		}


	}
}
