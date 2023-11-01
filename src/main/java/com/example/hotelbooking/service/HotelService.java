package com.example.hotelbooking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.example.hotelbooking.Constant;
import com.example.hotelbooking.entity.Customer;
import com.example.hotelbooking.entity.Hotel;
import com.example.hotelbooking.repository.HotelRepository;

import Utils.StringUtil;
import io.micrometer.common.util.StringUtils;

@Service
public class HotelService {
	private static final Logger log = LoggerFactory.getLogger(HotelService.class);
	
	@Autowired
	private HotelRepository repository;
	
	public Iterable<Hotel> getHotels(){
		return repository.findAll();
	}
	
	public Hotel getHotelById(String id) {
		return repository.findById(id).orElse(null);
	}
	
	public Hotel getHotelByName(String name) {
		return repository.findByName(name);
	}
	
	public JSONObject createHotel(Hotel hotel) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [0]Initialize
			String id = hotel.getId();
			String name = hotel.getName();
			Integer status = hotel.getStatus();
			
			// [1]Validation Check
			//--- NULL Check
			if(!StringUtil.areNotBlank(id, name) || status == null) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			//--- Duplicate Check
			Hotel existingHotel = getHotelByName(name);
			if(existingHotel != null) {
				responseJSON.put("code", Constant.RECORD_EXISTED_CODE);
				return responseJSON;
			}
			
			// [4] Return Data
			repository.save(hotel);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		
		return responseJSON;
	}
	
	public JSONObject updateHotel(Hotel hotel) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [0] Initialize
			String id = hotel.getId();
			String name = hotel.getName();
			String address = hotel.getAddress();
			String phone_no = hotel.getPhone_no();
			String email = hotel.getEmail();
			Integer status = hotel.getStatus();
			
			// [1] Validation Check
			//--- Existence Check
			Hotel existingHotel = getHotelById(id);
			if(existingHotel == null) {
				responseJSON.put("code", Constant.RECORD_NOT_EXIST_CODE);
				return responseJSON;
			}
			
			// [3] Modeling
			if(StringUtils.isNotBlank(name)) {
				//--- Existence Check "Name"
				Hotel duplicateNameHotel = getHotelByName(hotel.getName()); 
				if(duplicateNameHotel != null){
					responseJSON.put("code", Constant.RECORD_EXISTED_CODE);
					responseJSON.put("msg", "name existed, please try another");
					return responseJSON;
				}
				existingHotel.setName(name);
			}
			if(StringUtils.isNotBlank(address)) existingHotel.setAddress(address);
			if(StringUtils.isNotBlank(phone_no)) existingHotel.setPhone_no(phone_no);
			if(StringUtils.isNotBlank(email)) existingHotel.setEmail(email);
			existingHotel.setStatus(status != null? status: existingHotel.getStatus());
			
			// [4] Return Data
			repository.save(existingHotel);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}
	
	public JSONObject deleteHotel(String id) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [1] Validation Check
			//--- NULL Check
			if(!StringUtil.areNotBlank(id)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			//--- Existence Check
			Hotel existingHotel = getHotelById(id);
			if (existingHotel == null) {
				responseJSON.put("code", Constant.RECORD_NOT_EXIST_CODE);
				return responseJSON;
			}
			
			// [4] Return Data
			existingHotel.setStatus(-1);
			repository.save(existingHotel);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}
	
}
