package com.schooltraining.storesdistribution.controller;

import com.alibaba.fastjson.JSON;
import com.schooltraining.storesdistribution.entities.Authority;
import com.schooltraining.storesdistribution.entities.Msg;
import com.schooltraining.storesdistribution.entities.Role;
import com.schooltraining.storesdistribution.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @GetMapping("get")
    public List<Role> getRole(String roleName) {
        List<Role> roles = roleService.getRoles(roleName);
        if (roles != null) {
            return roles;
        }
        return null;
    }

    @PutMapping("update")
    public Object updateRole(Role role) {
        try{
            Map<String, String> map = new HashMap<>();
            if (roleService.update(role) > 0) {
                map.put("status", "success");
                return Msg.success(map);
            }
            return Msg.fail("失败了");
        }catch (Exception e){
            return Msg.fail(e.getMessage());
        }
    }

    @DeleteMapping("delete")
    public Object deleteRole(int roleId) {
        try{
            Map<String, String> map = new HashMap<>();
            if (roleService.delete(roleId) > 0) {
                map.put("status", "success");
                return Msg.success(map);
            }
            return Msg.fail("失败了");
        }catch (Exception e){
            return Msg.fail(e.getMessage());
        }
    }

    @PostMapping("add")
    public Object addRole(Role role) {
        try {
            role = roleService.add(role);
            if (role.getId() != null && role.getId() > 0) {//添加成功
                return Msg.success(role.getId());
            }
            return Msg.fail("插入失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Msg.fail(e.getMessage());
        }
    }

    @GetMapping("getRoles")
    public Object getRoles() {
        try {
            Map<String, String> map = roleService.getAll();
            Map<Integer, Role> mapRoles = new HashMap<>();
            map.values().forEach(roleStr ->{
                Role role = JSON.parseObject(roleStr, Role.class);
                mapRoles.put(role.getId(), role);
            });
            return Msg.success(mapRoles);
        } catch (Exception e) {
            e.printStackTrace();
            return Msg.fail(e.getMessage());
        }
    }
}
