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
    public List<Integer> getUserIds(Integer storeId) {
        return storeMapper.getUserIds(storeId);
    }

    @Override
    public List<Store> getAll() {
        try {
            //查看缓存中是否有数据
            jedis = redisUtil.getJedis();
            Map<String, String> storesStr = jedis.hgetAll(STORE_ALL_INFO);
//            System.out.println(storesStr);
            if (storesStr != null && storesStr.values().size() > 0) {
                List<Store> stores = new ArrayList<>();
                storesStr.values().forEach(storeStr -> {
                    Store store = JSON.parseObject(storeStr, Store.class);
                    stores.add(store);
                });
                return stores;
            }
            List<Store> stores = storeMapper.selectAll();
//            System.out.println(stores);
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
            int i = storeMapper.updateByPrimaryKeySelective(store);
            updateStoreCache(store, 1, null);
            return i;
        }
        return 0;
    }

    @Override
    public Store getStoreById(Integer storeId) {
        if (storeId != null) {
            Store store = storeMapper.selectByPrimaryKey(storeId);
            if (store != null) {
                return store;
            }
        }
        return null;
    }

    @Override
    public int addStore(Store store) {
        if(store != null){
            storeMapper.insertSelective(store);
            Integer storeId = store.getId();
            if (storeId != null && storeId > 0){
                //更新成功
                //更新缓存
                updateStoreCache(store, 1, null);
                return storeId;
            }
        }
        return 0;
    }

    @Override
    public int deleteStoreById(Integer id) {
        if (id != null && id != 0) {
            int i = storeMapper.deleteByPrimaryKey(id);
            if (i != 0) {
                //更新缓存
                updateStoreCache(null, 0, id);
                return i;
            }
        }
        return 0;
    }

    //更新缓存
    public void updateStoreCache(Store store, int status, Integer deleteId) {//status 1 : 添加/更新 0：删除
        try {
            jedis = redisUtil.getJedis();
            Map<String, String> storeStrMap = jedis.hgetAll(STORE_ALL_INFO);
            if (storeStrMap != null) {
                if (status == 1) {
                    storeStrMap.put(store.getId() + "", JSON.toJSONString(store));
                    jedis.hmset(STORE_ALL_INFO, storeStrMap);
                } else {//删除
                    jedis.hdel(STORE_ALL_INFO, deleteId + "");
                }
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
