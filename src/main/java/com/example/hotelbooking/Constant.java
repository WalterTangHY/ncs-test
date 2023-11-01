package com.example.hotelbooking;


import com.alibaba.fastjson.JSONObject;

import io.micrometer.common.util.StringUtils;

public class Constant {
	public static final int SUCCESS_CODE = 0;
	
	public static final int  CUSTOMER_NOT_EXIST_CODE = 301;
	public static final int  CUSTOMER_EXISTED_CODE = 302;
	public static final int  INVALID_SESSION_CODE = 303;
	public static final int  AUTHORIZE_FAILED_CODE = 304;
	
	public static final int EXCEED_LIMIT_CODE = 310;
	
	public static final int  RECORD_NOT_EXIST_CODE = 391;
	public static final int RECORD_EXISTED_CODE = 392;
	
	public static final int  BAD_REQUEST_CODE = 400;
	public static final int  TICKET_FINALIZED_CODE = 401;
	public static final int  INTERNAL_SERVER_ERROR = 500;
	
	public static String getErrorMsg(JSONObject result) {
		Integer code = result.getInteger("code");
		String msg = result.getString("msg");
		if(code == null || StringUtils.isNotBlank(msg)) {
			return StringUtils.isNotBlank(msg)? msg :"";
		}
		
		switch(code){
			case SUCCESS_CODE: return "Success";
			case BAD_REQUEST_CODE: return "Bad Request";
			case CUSTOMER_NOT_EXIST_CODE: return "Customer Not Exist";
			case CUSTOMER_EXISTED_CODE: return "Customer Existed";
			case INVALID_SESSION_CODE: return "Ïnvalid Session";
			case AUTHORIZE_FAILED_CODE: return "Authorize Failed";
			case EXCEED_LIMIT_CODE: return "Exceed Limit";
			case RECORD_NOT_EXIST_CODE: return "Record Not Exist";
			case RECORD_EXISTED_CODE: return "Record Existed";
			case TICKET_FINALIZED_CODE: return "Ticket Finalized";
			case INTERNAL_SERVER_ERROR: return "Internal Server Error";
		}
		return "";
	}
	
	
	

}
