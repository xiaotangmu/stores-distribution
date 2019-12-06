package com.schooltraining.storesdistribution.service.impl;

import com.schooltraining.storesdistribution.entities.User;
import com.schooltraining.storesdistribution.util.RedisUtil;

import redis.clients.jedis.Jedis;

public class JedisModule {

	RedisUtil redisUtils = null;
	
	public Object jedisModule(User user) {
		Jedis jedis = null;
		try {
			jedis = redisUtils.getJedis();
			
			
			return "";
		}finally {
			if(jedis != null) {
				jedis.close();
			}
		}
		
	}
}
