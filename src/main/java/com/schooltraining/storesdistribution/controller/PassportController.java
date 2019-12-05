package com.schooltraining.storesdistribution.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.schooltraining.storesdistribution.annotations.LoginRequired;
import com.schooltraining.storesdistribution.entities.User;
import com.schooltraining.storesdistribution.service.UserService;
import com.schooltraining.storesdistribution.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;

@Controller
public class PassportController {

	@Autowired
	UserService userService;

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
	public String login(User umsMember, HttpServletRequest request) {

		String token = "";

		// 调用用户服务验证用户名和密码
		User umsMemberLogin = userService.login(umsMember);
		System.out.println(umsMemberLogin);

		if (umsMemberLogin != null) {
			// 登录成功
			// 用jwt制作token
			String userId = umsMemberLogin.getId() + "";
			String userName = umsMemberLogin.getUserName();
			Map<String, Object> userMap = new HashMap<>();
			userMap.put("userId", userId);
			userMap.put("userName", userName);

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
			userService.addUserToken(token, userId);

		} else {
			// 登录失败
			token = "fail";
		}
		return token;
	}

//	@RequestMapping("index")
//	@LoginRequired(loginSuccess = false)
//	public String index(String ReturnUrl, ModelMap map) {
//
//		map.put("ReturnUrl", ReturnUrl);
//		return "login";
//	}
}
