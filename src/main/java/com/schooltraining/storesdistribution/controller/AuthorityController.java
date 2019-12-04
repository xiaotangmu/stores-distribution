package com.schooltraining.storesdistribution.controller;

import com.schooltraining.storesdistribution.entities.Authority;
import com.schooltraining.storesdistribution.entities.Msg;
import com.schooltraining.storesdistribution.service.AuthorityService;
import com.schooltraining.storesdistribution.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("auth")
public class AuthorityController {

    @Autowired
    AuthorityService authorityService;

    @GetMapping("getAll")
    public Object getAll(){
        try{
            return Msg.success(authorityService.getAll());
        }catch (Exception e){
            e.printStackTrace();
            return Msg.fail(e.getMessage());
        }
    }

}
