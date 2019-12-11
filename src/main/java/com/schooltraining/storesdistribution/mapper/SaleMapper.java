package com.schooltraining.storesdistribution.mapper;

import com.schooltraining.storesdistribution.entities.Sale;
import org.apache.ibatis.annotations.Param;

public interface SaleMapper {
    public Sale selectStoreTurnoverByYearAndMon(@Param("checkDate") String checkDate, @Param("storeId")Integer storeId);
}
