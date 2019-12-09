package com.schooltraining.storesdistribution.controller;

import com.schooltraining.storesdistribution.annotations.LoginRequired;
import com.schooltraining.storesdistribution.entities.Authority;
import com.schooltraining.storesdistribution.entities.Msg;
import com.schooltraining.storesdistribution.service.AuthorityService;
import com.schooltraining.storesdistribution.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins =  "*", maxAge = 3600)
@RequestMapping("auth")
public class AuthorityController {

    @Autowired
    AuthorityService authorityService;

    Map<String, Object> returnMap = null;

    @GetMapping("getAll")
    @LoginRequired
    public Object getAll(){
        returnMap = new HashMap<>();
        try{
            List<Authority> authorities = authorityService.getAll();
            returnMap.put("authorities", authorities);
            return Msg.success(returnMap);
        }catch (Exception e){
            e.printStackTrace();
            returnMap.put("message", "服务器异常");
            return Msg.fail(returnMap);
        }
    }

}
