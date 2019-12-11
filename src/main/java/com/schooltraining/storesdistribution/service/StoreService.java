package com.schooltraining.storesdistribution.service;

import com.schooltraining.storesdistribution.entities.Store;

import java.util.List;

public interface StoreService {

    public List<Integer> getUserIds(Integer storeId);

    List<Store> getAll();

    int updateStore(Store store);

    Store getStoreById(Integer storeId);

    int addStore(Store store);

    int deleteStoreById(Integer id);
}
