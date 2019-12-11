package com.schooltraining.storesdistribution.service.impl;

import com.schooltraining.storesdistribution.entities.Sale;
import com.schooltraining.storesdistribution.mapper.SaleMapper;
import com.schooltraining.storesdistribution.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SaleServiceImpl implements SaleService {

    @Autowired
    SaleMapper saleMapper;

    @Override
    public Sale getTurnover(String checkDate, Integer storeId) {
        if(checkDate == null || storeId == null){
            return null;
        }
        Sale sale = saleMapper.selectStoreTurnoverByYearAndMon(checkDate, storeId);
        if(sale == null || sale.getTurnover() == null){//没有数据处理
            //2019-09
            String[] split = checkDate.split("-");
            sale.setYear(split[0]);
            sale.setMon(split[1]);
            sale.setOrderNum(0);
            sale.setTurnover(new BigDecimal(0));
            sale.setStoreId(storeId);
        }
        return sale;
    }
}
