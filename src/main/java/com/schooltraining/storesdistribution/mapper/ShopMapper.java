package com.schooltraining.storesdistribution.mapper;

import com.schooltraining.storesdistribution.entities.Shop;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ShopMapper extends Mapper<Shop>{

    public List<Integer> getUserIds (Integer shopId);
}
