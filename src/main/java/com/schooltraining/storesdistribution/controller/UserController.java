package com.schooltraining.storesdistribution.controller;

import com.alibaba.fastjson.JSON;
import com.schooltraining.storesdistribution.annotations.LoginRequired;
import com.schooltraining.storesdistribution.entities.Msg;
import com.schooltraining.storesdistribution.entities.User;
import com.schooltraining.storesdistribution.service.UserService;
import com.schooltraining.storesdistribution.util.CookieUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("user")
@CrossOrigin(origins =  "*", maxAge = 3600)
public class UserController {
	
	@Autowired
	UserService userService;

	Map<String, Object> returnMap = null;

	//注册register
	@PostMapping("regist")
	public Object register(User user) {
		returnMap = new HashMap<>();
		try {
			System.out.println(user);
			//防止恶意操作 --- 没有拦截
			if(StringUtils.isBlank(user.getUserName()) || StringUtils.isBlank(user.getPassword())) {
				return Msg.fail("...");
			}
			//格式校验
			//...
			
			//判断用户是否已经存在
			int i = userService.getUserByUserName(user.getUserName());
			if(i > 0) {
				returnMap.put("message", "用户已存在");
				return Msg.fail(returnMap); 
			}
			//注册用户
			User user2 = userService.addUser(user);
			System.out.println(user2);
			if(user2.getId() > 0) {
				returnMap.put("status", "success");
				return Msg.success(returnMap);
			}
			returnMap.put("message", "插入出错了");
			return Msg.fail(returnMap); 
		} catch(Exception e) {
			e.printStackTrace();
			returnMap.put("message", "服务器异常");
			return Msg.fail(returnMap);
		}
	}
	
	//更新户角色
	@PutMapping("userRole/{id}")
	@LoginRequired
	public Object updateUserRole(@PathVariable int id, int roleId) {
		returnMap = new HashMap<>();
		try {
			System.out.println("userId: " + id + ", roleId: " + roleId);
//			userService.updateRole(id, roleId);
			
			returnMap.put("message", "获取数据失败");
			return Msg.fail(returnMap);
		} catch(Exception e) {
			e.printStackTrace();
			returnMap.put("message", "服务器异常");
			return Msg.fail(returnMap);
		}
	}
	
	@GetMapping("users")
	@LoginRequired
	public Object getAllUsers() {
		returnMap = new HashMap<>();
		try {
			List<User> users = userService.getAll();
			if(users != null && users.size() > 0) {
				returnMap.put("users", users);
				return Msg.success(returnMap);
			}
			returnMap.put("message", "获取数据失败");
			return Msg.fail(returnMap);
		} catch(Exception e) {
			e.printStackTrace();
			returnMap.put("message", "服务器异常");
			return Msg.fail(returnMap);
		}
	}
    
    @GetMapping("user/info")
    @LoginRequired
    public Object info(HttpServletRequest request){
    	returnMap = new HashMap<>();
        try{
        	String userId = (String)request.getAttribute("userId");
        	//获取用户信息
        	User user = userService.getUserInfo(Integer.parseInt(userId));
        	if(user != null) {
				returnMap.put("authorities", user.getRole().getAuthorities());
				returnMap.put("info", user);
        		return Msg.success(returnMap);
        	}
        	returnMap.put("message", "获取不到数据");
        	return Msg.fail(returnMap);
            
        }catch (Exception e){
        	e.printStackTrace();
        	returnMap.put("message", "服务器异常");
            return Msg.fail(returnMap);
        }
    }
    
    @PostMapping("user/update")
    @LoginRequired
//    public Object update(@RequestBody(required = true) String updateUser, HttpServletRequest request){
    public Object update(User user, HttpServletRequest request){
    	returnMap = new HashMap<>();
        try{
//        	User user = JSON.parseObject(updateUser, User.class);
//        	System.out.println(request.getAttribute("userId"));
        	System.out.println(user);
        	String userId = (String) request.getAttribute("userId");
			user.setId(Integer.parseInt(userId));
        	int i = userService.update(user);
        	if(i != 0) {
        		return Msg.success("success");
        	}
        	returnMap.put("message", "更新失败");
        	return Msg.fail(returnMap);
            
        }catch (Exception e){
        	e.printStackTrace();
        	returnMap.put("message", "服务器异常");
            return Msg.fail(returnMap);
        }
    }

}
