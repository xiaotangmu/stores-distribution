package com.schooltraining.storesdistribution.service.impl;

import com.alibaba.fastjson.JSON;
import com.schooltraining.storesdistribution.entities.User;
import com.schooltraining.storesdistribution.mapper.UserMapper;
import com.schooltraining.storesdistribution.service.UserService;
import com.schooltraining.storesdistribution.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

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
            return selectByExample.get(0);
        }
        return null;

    }
}
