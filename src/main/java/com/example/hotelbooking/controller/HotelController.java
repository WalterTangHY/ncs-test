package com.example.hotelbooking.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.example.hotelbooking.Constant;
import com.example.hotelbooking.entity.Hotel;
import com.example.hotelbooking.service.HotelService;

import Utils.StringUtil;
import io.micrometer.common.util.StringUtils;

@Controller 
@RequestMapping(path="/hotel") // This means URL's start with /customer (after Application path)
public class HotelController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(HotelController.class);
	
	@Autowired
	private HotelService hotelService;

	// [READ]
	@GetMapping(path="/all")
	public @ResponseBody JSONObject getAllHotel() {
		JSONObject responseJSON = new JSONObject(true);
		try {
			Iterable<Hotel> hotels = hotelService.getHotels();
			responseJSON.put("code", Constant.SUCCESS_CODE);
			responseJSON.put("data", hotels);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
	    return ret(responseJSON);
	}
	
	@GetMapping(path="")
	public @ResponseBody JSONObject getHotel(@RequestParam String id){
		JSONObject responseJSON = new JSONObject(true);
		try {
			Hotel hotel = hotelService.getHotelById(id);
			responseJSON.put("code", Constant.SUCCESS_CODE);
			responseJSON.put("data", hotel);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return ret(responseJSON);
	}
	
	// [CREATE]
	@PostMapping(path="/add") // Map ONLY POST Requests
	public @ResponseBody JSONObject createHotel (@RequestBody Hotel hotel) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			//Modeling
			Hotel model = new Hotel();
		    model.setId(StringUtil.shortUUIDWithDate());
		    model.setName(hotel.getName());
		    model.setAddress(hotel.getAddress());
		    model.setPhone_no(hotel.getPhone_no());
		    model.setEmail(hotel.getEmail());
		    model.setStatus(1);
		    model.setCreate_date(new Date());
		    
		    responseJSON = hotelService.createHotel(model);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
	    return ret(responseJSON);
	}
	
	// [UPDATE]
	@PostMapping(path="/edit") // Map ONLY POST Requests
	public @ResponseBody JSONObject updateHotel (@RequestBody Hotel hotel) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [0]Initialize
			String id = hotel.getId();
			
			// [1] Validation Checking
			if(StringUtils.isBlank(id)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				responseJSON.put("msg", "id needed for verification");
				return responseJSON;
			}
			
			// [3] Modeling
		    Hotel model = new Hotel();
		    model.setId(id);//Identification Checking
		    model.setName(hotel.getName());
		    model.setAddress(hotel.getAddress());
		    model.setPhone_no(hotel.getPhone_no());
		    model.setEmail(hotel.getEmail());
		    model.setStatus(hotel.getStatus());
		    responseJSON = hotelService.updateHotel(model);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
	    return ret(responseJSON);
	}
	
	// [DELETE]
	@PostMapping(path="/remove")
	public @ResponseBody JSONObject deleteHotel(@RequestBody Hotel hotel) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [0]Initialize
			String id = hotel.getId();
					
			// [1] Validation Checking
			if(StringUtils.isBlank(id)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				responseJSON.put("msg", "id needed for verification");
				return responseJSON;
			}
			responseJSON = hotelService.deleteHotel(id);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
	    return ret(responseJSON);
	}
	
}
