package com.schooltraining.storesdistribution.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.schooltraining.storesdistribution.entities.Authority;
import com.schooltraining.storesdistribution.mapper.AuthorityMapper;
import com.schooltraining.storesdistribution.service.AuthorityService;
import com.schooltraining.storesdistribution.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AuthorityMapper authorityMapper;

    @Override
    public List<Authority> getAll() {
        List<Authority> authorities = null;
        Jedis jedis = null;
        RLock lock = null;
        boolean tryLock = false;

        try {
            //从缓存中获取
            jedis = redisUtil.getJedis();
            String authCacheStr = jedis.get("authority:all:info");
            if (StringUtils.isNotBlank(authCacheStr)) {//缓存中有数据
                authorities = JSON.parseObject(authCacheStr, new TypeReference<List<Authority>>() {
                });
                return authorities;
            }

            //同步到缓存
            // 设置分布式锁
            lock = redissonClient.getLock("authority:all:lock");// 声明锁
            tryLock = lock.tryLock();
            if (tryLock) {//成功上锁
                //缓存中没有数据,从数据库中获取
                authorities = authorityMapper.selectAll();

                if (authorities != null && authorities.size() > 0) {
                    // mysql查询结果存入redis
                    jedis.set("authority:all:info", JSON.toJSONString(authorities));
                } else {
                    // 数据库没有
                    // 为了防止缓存穿透将，null或者空字符串值设置给redis
                    jedis.setex("authority:all:info", 60 * 3, JSON.toJSONString(""));
                }
            } else {//有锁,自旋
                return getAll();
            }

            return authorities;
        }  finally {
            if (tryLock) {
                lock.unlock();// 解锁
            }
            jedis.close();
        }
    }
}
