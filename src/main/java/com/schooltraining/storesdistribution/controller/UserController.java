package com.schooltraining.storesdistribution.controller;

import com.schooltraining.storesdistribution.entities.Msg;
import com.schooltraining.storesdistribution.entities.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    @GetMapping("login")
    public Object login(User user){
        try{

            return Msg.success("");
        }catch (Exception e){
            return Msg.fail(e.getMessage());
        }
    }
    @GetMapping("info")
    public Object info(User user){
        try{

            return Msg.success("");
        }catch (Exception e){
            return Msg.fail(e.getMessage());
        }
    }
    @GetMapping("logout")
    public Object logout(User user){
        try{

            return Msg.success("");
        }catch (Exception e){
            return Msg.fail(e.getMessage());
        }
    }

    @GetMapping("update")
    public Object update(User user){
        try{

            return Msg.success("");
        }catch (Exception e){
            return Msg.fail(e.getMessage());
        }
    }
}
