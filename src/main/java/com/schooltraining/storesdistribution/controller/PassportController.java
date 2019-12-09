package com.schooltraining.storesdistribution.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.schooltraining.storesdistribution.annotations.LoginRequired;
import com.schooltraining.storesdistribution.entities.Msg;
import com.schooltraining.storesdistribution.entities.Role;
import com.schooltraining.storesdistribution.entities.User;
import com.schooltraining.storesdistribution.service.RoleService;
import com.schooltraining.storesdistribution.service.UserService;
import com.schooltraining.storesdistribution.util.CookieUtil;
import com.schooltraining.storesdistribution.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;

@Controller
@RequestMapping("user")
@CrossOrigin(origins =  "*", maxAge = 3600)
public class PassportController {

	@Autowired
	UserService userService;
	
	@Autowired
	RoleService roleService;
	
	Map<String, Object> returnMap = null;

	@RequestMapping("verify")
	@ResponseBody
	public String verify(String token, String currentIp, HttpServletRequest request) {

		// 通过jwt校验token真假
		Map<String, String> map = new HashMap<>();

		Map<String, Object> decode = JwtUtil.decode(token, "storesDistribution2019", currentIp);// key 和 ip 在实际生产要再加密

		if (decode != null) {
			map.put("status", "success");
			map.put("userId", (String) decode.get("userId"));
			map.put("userName", (String) decode.get("userName"));
		} else {
			map.put("status", "fail");
		}

		return JSON.toJSONString(map);
	}

	@PostMapping("login")
	@ResponseBody
//	public Object login(@RequestBody(required = true) String userLogin, HttpServletRequest request, HttpServletResponse response) {
	public Object login(User user, HttpServletRequest request, HttpServletResponse response) {
		try {
			System.out.println(user);
//			System.out.println(userLogin);
//			User user = JSON.parseObject(userLogin, User.class);
			Map<String, Object> mapToken = new HashMap<>();
			//查看是否存在用户
			int i = userService.getUserByUserName(user.getUserName());
//			System.out.println(i);
			if(i == 0) {
				mapToken.put("message", "该账号不存在");
				return Msg.fail(mapToken);
			}

			String token = "";

			// 调用用户服务验证用户名和密码
			User umsMemberLogin = new User();
			umsMemberLogin.setUserName(user.getUserName());
			umsMemberLogin.setPassword(user.getPassword());
			umsMemberLogin = userService.login(umsMemberLogin);
//			System.out.println(umsMemberLogin);

			Map<String, Object> userMap = new HashMap<>();
			if (umsMemberLogin != null) {
				// 登录成功
				// 用jwt制作token
				String userIdStr = umsMemberLogin.getId() + "";
				String userNameStr = umsMemberLogin.getUserName();
				String storeIdStr = umsMemberLogin.getStoreId() + "";

				userMap.put("userId", userIdStr);
				userMap.put("userName", userNameStr);
				userMap.put("storeId", storeIdStr);

				String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端ip
				if (StringUtils.isBlank(ip)) {
					ip = request.getRemoteAddr();// 从request中获取ip
					if (StringUtils.isBlank(ip)) {
						ip = "127.0.0.1";//都没有，出错，这里直接给了
					}
				}

				// 按照设计的算法对参数进行加密后，生成token
				token = JwtUtil.encode("storesDistribution2019", userMap, ip);

				//将token存入redis一份
				userService.addUserToken(token, userIdStr);

			} else {
				// 登录失败
				mapToken.put("message", "账号或密码有误");
				return Msg.fail(mapToken);
//				token = "fail";
			}

			//获取该用户角色信息
//			List<Role> roles = userService.getRolesByUserIds(umsMemberLogin.getId());
			Role userRole = roleService.getRoleById(umsMemberLogin.getRoleId());
//			System.out.println(roles);

			//缓存用户信息
//			umsMemberLogin.setRoles(roles);
			umsMemberLogin.setRole(userRole);

			userService.setUserCache(umsMemberLogin);

			mapToken.putAll(userMap);
			mapToken.put("role", userRole);
			mapToken.put("token", token);

			//记录cookie
			CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 *2, true);
			return Msg.success(mapToken);
		}catch(Exception e) {
			Map<String, Object> tokenMap = new HashMap<>();
			tokenMap.put("message", "服务器异常");
			return Msg.fail(tokenMap);
		}
	}

    @GetMapping("logout")
	@ResponseBody
	@LoginRequired
    public Object logout(HttpServletRequest request, HttpServletResponse response){
    	returnMap = new HashMap<>();
        try{
//			System.out.println("hello");
			String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        	if(oldToken != null) {
        		//将cookie移除
        		CookieUtil.deleteCookie(request, response, "oldToken");
        	}
        	
        	return Msg.success("success");
            
        }catch (Exception e){
        	e.printStackTrace();
        	returnMap.put("message", "服务器异常");
            return Msg.fail(returnMap);
        }
    }
    
//	@RequestMapping("index")
//	@LoginRequired(loginSuccess = false)
//	public String index(String ReturnUrl, ModelMap map) {
//
//		map.put("ReturnUrl", ReturnUrl);
//		return "login";
//	}
}
