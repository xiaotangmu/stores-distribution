package com.schooltraining.storesdistribution.controller;

import com.alibaba.fastjson.JSON;
import com.schooltraining.storesdistribution.annotations.LoginRequired;
import com.schooltraining.storesdistribution.entities.Authority;
import com.schooltraining.storesdistribution.entities.Msg;
import com.schooltraining.storesdistribution.entities.Role;
import com.schooltraining.storesdistribution.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("role")
public class RoleController {

    @Autowired
    RoleService roleService;

    Map<String, Object> returnMap = null;

    @GetMapping("get")
    @LoginRequired
    public List<Role> getRole(String roleName) {
        List<Role> roles = roleService.getRoles(roleName);
        if (roles != null) {
            return roles;
        }
        return null;
    }

    @PostMapping("update")
    @LoginRequired
    public Object updateRole(Role role) {
        try {
//            System.out.println(role);
            if(role.getId() == null){
                return Msg.fail("id为空");
            }
            List<Integer> authorityIds = splitStr(role.getAuthorityIds());
//            System.out.println(authorityIds);
            Map<String, String> map = new HashMap<>();
            if (roleService.update(role, authorityIds) != 0) {
                map.put("status", "success");
                return Msg.success(map);
            }
            return Msg.fail("失败了");
        } catch (Exception e) {
            return Msg.fail(e.getMessage());
        }
    }

    @PostMapping("delete")
    @LoginRequired
    public Object deleteRole(int roleId) {
        try {
//            System.out.println(roleId);
            Map<String, String> map = new HashMap<>();
            if (roleService.delete(roleId) != 0) {
                map.put("status", "success");
                return Msg.success(map);
            }
            return Msg.fail("失败了");
        } catch (Exception e) {
            return Msg.fail(e.getMessage());
        }
    }

    @PostMapping("add")
    @LoginRequired
    public Object addRole(Role role) {
//    public Object addRole(@RequestBody(required = true) String resData) {
        try {
//            System.out.println(role);
            String idsStr = role.getAuthorityIds();
//            List<Integer> authorityIds = new ArrayList<>();
//            if(StringUtils.isNotBlank(idsStr)){
//                String[] split = idsStr.split("/");
//                for (int i = 0; i < split.length; i++){
//                    authorityIds.add(Integer.parseInt(split[i]));
//                }
//            }
            List<Integer> authorityIds = splitStr(idsStr);
//            System.out.println(authorityIds);
            role = roleService.add(role, authorityIds);
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
    @LoginRequired
    public Object getRoles() {
        returnMap = new HashMap<>();
        try {
            Map<String, String> map = roleService.getAll();
//            Map<Integer, Role> mapRoles = new HashMap<>();
            List<Role> mapRoles = new ArrayList<>();
            map.values().forEach(roleStr -> {
                Role role = JSON.parseObject(roleStr, Role.class);
                mapRoles.add(role);
//                mapRoles.put(role.getId(), role);
            });
            returnMap.put("roles", mapRoles);
            return Msg.success(returnMap);
//            return Msg.success(mapRoles);
        } catch (Exception e) {
            e.printStackTrace();
            return Msg.fail(e.getMessage());
        }
    }

    public List<Integer> splitStr(String idsStr){
        List<Integer> authorityIds = new ArrayList<>();
        if(StringUtils.isNotBlank(idsStr)){
            String[] split = idsStr.split("/");
            for (int i = 0; i < split.length; i++){
                authorityIds.add(Integer.parseInt(split[i]));
            }
        }
        return authorityIds;
    }
}
