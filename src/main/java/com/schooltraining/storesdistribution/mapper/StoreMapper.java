package com.schooltraining.storesdistribution.mapper;

import com.schooltraining.storesdistribution.entities.Store;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface StoreMapper extends Mapper<Store>{

    public List<Integer> getUserIds (Integer store);
}
