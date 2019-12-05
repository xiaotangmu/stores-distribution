package com.schooltraining.storesdistribution.service.impl;

import com.schooltraining.storesdistribution.mapper.ShopMapper;
import com.schooltraining.storesdistribution.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService{

    @Autowired
    ShopMapper shopMapper;

    @Override
    public List<Integer> getUserIds(Integer shopId){
        return shopMapper.getUserIds(shopId);
    }

}
