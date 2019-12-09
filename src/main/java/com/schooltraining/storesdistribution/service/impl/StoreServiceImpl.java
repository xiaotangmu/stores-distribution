package com.schooltraining.storesdistribution.service.impl;

import com.alibaba.fastjson.JSON;
import com.schooltraining.storesdistribution.entities.Store;
import com.schooltraining.storesdistribution.mapper.StoreMapper;
import com.schooltraining.storesdistribution.service.StoreService;
import com.schooltraining.storesdistribution.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    StoreMapper storeMapper;

    @Autowired
    RedisUtil redisUtil;

    Jedis jedis = null;

    private static final String STORE_ALL_INFO = "store:all:info";

    @Override
    public List<Integer> getUserIds(Integer storeId){
        return storeMapper.getUserIds(storeId);
    }

    @Override
    public List<Store> getAll() {
        try{
            //查看缓存中是否有数据
            jedis = redisUtil.getJedis();
            Map<String, String> storesStr = jedis.hgetAll(STORE_ALL_INFO);
            if (storesStr != null) {
                List<Store> stores = new ArrayList<>();;
                storesStr.values().forEach(storeStr -> {
                    Store store = JSON.parseObject(storeStr, Store.class);
                    stores.add(store);
                });
                return stores;
            }
            List<Store> stores = storeMapper.selectAll();
            if (stores != null && stores.size() > 0) {
                Map<String, String> storesMapFormDB = new HashMap<>();
                stores.forEach(store -> {
                    storesMapFormDB.put(store.getId() + "", JSON.toJSONString(store));
                });
                //保存到缓存
                jedis.hmset(STORE_ALL_INFO, storesMapFormDB);
                return stores;
            }
            //防止击穿
            jedis.hmset(STORE_ALL_INFO, null);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }

    @Override
    public int updateStore(Store store) {
        if (store.getId() != null) {
            return storeMapper.updateByPrimaryKeySelective(store);
        }
        return 0;
    }

    @Override
    public Store getStoreById(Integer storeId) {
        if (storeId != null) {
            Store store = storeMapper.selectByPrimaryKey(storeId);
            if (store != null){
                return store;
            }
        }
        return null;
    }

}
