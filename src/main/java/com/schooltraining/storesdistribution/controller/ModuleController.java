package com.schooltraining.storesdistribution.controller;

import java.util.HashMap;
import java.util.Map;


import com.schooltraining.storesdistribution.entities.Msg;

public class ModuleController { //用来复制粘贴的

	Map<String, Object> returnMap = null;
	
	public Object module() {
		returnMap = new HashMap<>();
		try {
			
			returnMap.put("message", "获取数据失败");
			return Msg.fail(returnMap);
		} catch(Exception e) {
			e.printStackTrace();
			returnMap.put("message", "服务器异常");
			return Msg.fail(returnMap);
		}
	}
}
