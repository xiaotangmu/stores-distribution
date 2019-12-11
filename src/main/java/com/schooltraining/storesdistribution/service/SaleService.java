package com.schooltraining.storesdistribution.service;

import com.schooltraining.storesdistribution.entities.Sale;

public interface SaleService {

    public Sale getTurnover(String checkDate, Integer storeId);
}
