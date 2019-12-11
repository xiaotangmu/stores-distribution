package com.schooltraining.storesdistribution.controller;

import com.schooltraining.storesdistribution.annotations.LoginRequired;
import com.schooltraining.storesdistribution.entities.Msg;
import com.schooltraining.storesdistribution.entities.User;
import com.schooltraining.storesdistribution.service.IntegrationService;
import com.schooltraining.storesdistribution.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("integration")
@CrossOrigin(origins =  "*", maxAge = 3600)
public class IntegrationController {

    @Autowired
    IntegrationService integrationService;

    @Autowired
    UserService userService;

    Map<String, Object> returnMap = null;

    //积分兑换，减少积分
    @PostMapping("subtract")
    @LoginRequired
    public Object subtractIntegration(int integration, HttpServletRequest request) {
        returnMap = new HashMap<>();
        try {
//            System.out.println(integration);
            String userId = (String)request.getAttribute("userId");
//            System.out.println(userId);
            User user = userService.getUser(Integer.parseInt(userId));
            return doIntegration(user, integration, 2);//-
        } catch(Exception e) {
            e.printStackTrace();
            returnMap.put("message", "服务器异常");
            return Msg.fail(returnMap);
        }
    }

    //增加会员积分
    @PostMapping("add")
    @LoginRequired
    public Object addIntegration(int integration, HttpServletRequest request) {
        returnMap = new HashMap<>();
        try {
            String userId = (String)request.getAttribute("userId");
            User user = userService.getUser(Integer.parseInt(userId));
            return doIntegration(user, integration, 1);//+
        } catch(Exception e) {
            e.printStackTrace();
            returnMap.put("message", "服务器异常");
            return Msg.fail(returnMap);
        }
    }

    public Object doIntegration(User user, int integration, int flag){//flag: 1 : + ; 2 : -
        returnMap = new HashMap<>();
        if (user != null) {
            Long oldIntegration = user.getIntegration();
            if(oldIntegration == null){
                oldIntegration = 0L;
            }
            if(flag == 1){
                user.setIntegration(oldIntegration + integration);
            }else{
                long result = oldIntegration - integration;
                if(result < 0){
                    returnMap.put("message", "积分不够");
                    return Msg.fail(returnMap);
                }
                user.setIntegration(result);
            }
            int i = userService.update(user);
            System.out.println(user);
            System.out.println(i);
            if (i != 0) {
                returnMap.put("status", "success");
                return Msg.success(returnMap);
            }
        }
        returnMap.put("message", "系统出错了");
        return Msg.fail(returnMap);
    }
}
