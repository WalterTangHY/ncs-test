package com.example.hotelbooking.controller;

import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.example.hotelbooking.Constant;

public class BaseController{
	public static final String codeKey = "code";
	public static final String msgKey = "msg";
	
	public static JSONObject ret(JSONObject result) {
		JSONObject sortJSON = new JSONObject(true);
		sortJSON.put(codeKey, result.getInteger(codeKey));
		sortJSON.put(msgKey, Constant.getErrorMsg(result));
		
		Set<String> keys = result.keySet();
		keys.forEach( key ->{
			if(key.equals(codeKey) || key.equals(msgKey)) return;
			sortJSON.put(key, result.get(key));
		});
		return sortJSON;
	}

}
