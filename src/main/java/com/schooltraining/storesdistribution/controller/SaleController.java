package com.schooltraining.storesdistribution.controller;

import com.schooltraining.storesdistribution.annotations.LoginRequired;
import com.schooltraining.storesdistribution.entities.Msg;
import com.schooltraining.storesdistribution.entities.Sale;
import com.schooltraining.storesdistribution.entities.Store;
import com.schooltraining.storesdistribution.mapper.SaleMapper;
import com.schooltraining.storesdistribution.service.SaleService;
import com.schooltraining.storesdistribution.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("sale")
@CrossOrigin(origins =  "*", maxAge = 3600)
public class SaleController {

    Map<String, Object> returnMap = null;

    @Autowired
    SaleService saleService;

    @Autowired
    StoreService storeService;

    //获取每月的销售额
    @GetMapping("getTurnover")
    @LoginRequired
    public Object getTurnover() {
        returnMap = new HashMap<>();
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            //获取当前年
            int yearStr = calendar.get(Calendar.YEAR);
            //获取当前月
            int monStr = calendar.get(Calendar.MONTH) + 1;
            //得到几年已过月份及当前月
            List<String> mons = new ArrayList();
            for (int i = 1; i <= monStr; i++){
                //判断是否小于10 -- 小于的个位 -- 需要表示为 0x
                if(i < 10){
                    mons.add("0" + i);
                }else{
                    mons.add(i + "");
                }
            }
            //拼接查询字符串 checkDate 2019-08
            List<String> checkDates = new ArrayList<>();
            mons.forEach(mon -> {
                checkDates.add(yearStr + "-" + mon);
            });
            //获取数据库数据
            //获取所有店铺
            List<Store> stores = storeService.getAll();
            Map<Integer, List<Sale>> salesMap = new HashMap<>();//用来保存输出数据
            //查询月销售数据 -- 分店获取
            stores.forEach(store -> {
                List<Sale> sales = new ArrayList<>();
                Integer storeId = store.getId();
                String storeName = store.getStoreName();
//                System.out.println(storeId);
                checkDates.forEach(checkDate -> {
//                    System.out.println(checkDate);
                    Sale sale = saleService.getTurnover(checkDate, storeId);
                    sale.setStoreName(storeName);
                    sales.add(sale);
                });
                salesMap.put(storeId, sales);
            });
            return Msg.success(salesMap);
        } catch(Exception e) {
            e.printStackTrace();
            returnMap.put("message", "服务器异常");
            return Msg.fail(returnMap);
        }
    }
}
