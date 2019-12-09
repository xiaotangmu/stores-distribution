package com.schooltraining.storesdistribution.controller;

import com.alibaba.fastjson.JSON;
import com.schooltraining.storesdistribution.annotations.LoginRequired;
import com.schooltraining.storesdistribution.entities.Msg;
import com.schooltraining.storesdistribution.entities.Store;
import com.schooltraining.storesdistribution.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("store")
public class StoreController {

    Map<String, Object> returnMap = null;

    @Autowired
    StoreService storeService;

    @PostMapping("getStore")
    @LoginRequired
//    public Object getStore(String storeId){
    public Object getStore(Integer storeId){
        returnMap = new HashMap<>();
        try {
            System.out.println(storeId);
            Store store = storeService.getStoreById(storeId);
            if (store != null) {
                returnMap.put("store", store);
                return Msg.success(returnMap);
            }
            returnMap.put("message", "出错了");
            return Msg.success(returnMap);
        } catch(Exception e) {
            e.printStackTrace();
            returnMap.put("message", "服务器异常");
            return Msg.fail(returnMap);
        }
    }

    //更新店铺信息
    @PostMapping("update")
    @LoginRequired
    public Object update(Store store){
//    public Object update(String storeStr){
        returnMap = new HashMap<>();
        try {
//            System.out.println(storeStr);
//            Store store = JSON.parseObject(storeStr, Store.class);
            System.out.println(store);
            int i = storeService.updateStore(store);
            if(i != 0){
                returnMap.put("status", "success");
                return Msg.success(returnMap);
            }
            returnMap.put("message", "数据有误");
            return Msg.fail(returnMap);
        } catch(Exception e) {
            e.printStackTrace();
            returnMap.put("message", "服务器异常");
            return Msg.fail(returnMap);
        }
    }

    //获取所有店铺信息
    @GetMapping("getAll")
    @LoginRequired
    public Object module() {
        returnMap = new HashMap<>();
        try {
            List<Store> stores = storeService.getAll();
            System.out.println(stores);
            returnMap.put("stores", stores);
            return Msg.success(returnMap);
        } catch(Exception e) {
            e.printStackTrace();
            returnMap.put("message", "服务器异常");
            return Msg.fail(returnMap);
        }
    }
}
