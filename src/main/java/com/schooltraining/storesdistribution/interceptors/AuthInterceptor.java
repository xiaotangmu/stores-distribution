package com.schooltraining.storesdistribution.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.schooltraining.storesdistribution.annotations.LoginRequired;
import com.schooltraining.storesdistribution.util.CookieUtil;
import com.schooltraining.storesdistribution.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
        // 拦截代码
        // 判断被拦截的请求的访问的方法的注解(是否时需要拦截的)
        HandlerMethod hm = (HandlerMethod) handler;
		LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);

        StringBuffer url = request.getRequestURL();
        System.out.println(url);

        // 是否拦截
        if (methodAnnotation == null) {
            return true;
        }

		String token = "";

		String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
		if (StringUtils.isNotBlank(oldToken)) {
			token = oldToken;
		}
//		System.out.println(oldToken);
		String newToken = request.getParameter("token");
//		String newToken = request.getParameter("userUpadate");
		if (StringUtils.isNotBlank(newToken)) {
			token = newToken;
		}
//		System.out.println(newToken);

		// 是否必须登录
		boolean loginSuccess = methodAnnotation.loginSuccess();// 获得该请求是否必登录成功

		// 调用认证中心进行验证
		String success = "fail";
		Map<String, String> successMap = new HashMap<>();
		if (StringUtils.isNotBlank(token)) {
			String ip = request.getHeader("x-forwarded-for");// 通过nginx转发的客户端
			if (StringUtils.isBlank(ip)) {// 没有用nginx 转发
				ip = request.getRemoteAddr();// 从request中获取ip
				if (StringUtils.isBlank(ip)) {// 都没有，说明出错了，实际生产直接return
                	ip = "127.0.0.1";
//					return false;
				}
			}
			String successJson = HttpclientUtil
					.doGet("http://localhost:8081/stores/user/verify?token=" + token + "&currentIp=" + ip);

			successMap = JSON.parseObject(successJson, Map.class);

			success = successMap.get("status");

		}

		if (loginSuccess) {
			// 必须登录成功才能使用
			if (!success.equals("success")) {
				// 重定向passport登录
//				StringBuffer requestURL = request.getRequestURL();
//				response.sendRedirect("http://localhost:8081/stores/index?ReturnUrl=" + requestURL);
				return false;
			}

			// 需要将token携带的用户信息写入
			request.setAttribute("userId", successMap.get("userId"));
			request.setAttribute("userName", successMap.get("userName"));
			request.setAttribute("storeId", successMap.get("storeId"));
			// 验证通过，覆盖cookie中的token
			if (StringUtils.isNotBlank(token)) {
				CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);//设置2小时后过期
			}

		} else {
			// 没有登录也能用，但是必须验证
			if (success.equals("success")) {
				// 需要将token携带的用户信息写入
				request.setAttribute("userId", successMap.get("userId"));
				request.setAttribute("userName", successMap.get("userName"));
				request.setAttribute("storeId", successMap.get("storeId"));

				// 验证通过，覆盖cookie中的token
				if (StringUtils.isNotBlank(token)) {
					CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
				}

			}
		}

		return true;
	}
}