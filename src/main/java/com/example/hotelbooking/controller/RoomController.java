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
import com.example.hotelbooking.entity.Room;
import com.example.hotelbooking.service.RoomService;

import Utils.StringUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;

@Controller
@RequestMapping(path="/room")
public class RoomController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(RoomController.class);
	
	@Autowired
	private RoomService roomService;
	
	// [READ]
	@GetMapping(path="/all")
	public @ResponseBody JSONObject getAllRoom(@RequestParam String hotelid) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			Iterable<Room> rooms = roomService.getAllRooms(hotelid);
			responseJSON.put("code", Constant.SUCCESS_CODE);
			responseJSON.put("data", rooms);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
	    return ret(responseJSON);
	}
	
	@GetMapping(path="")
	public @ResponseBody JSONObject getRoom(@RequestParam String id){
		JSONObject responseJSON = new JSONObject(true);
		try {
			Room room = roomService.getRoomById(id);
			responseJSON.put("code", Constant.SUCCESS_CODE);
			responseJSON.put("data", room);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}
	
	// [CREATE]
	@PostMapping(path="/add") // Map ONLY POST Requests
	public @ResponseBody JSONObject createRoom(@Valid @RequestBody Room room) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			//Modeling
			Room model = new Room();
			model.setId(StringUtil.shortUUIDWithDate());
		    model.setRoomno(room.getRoomno());
		    model.setHotelid(room.getHotelid());
		    model.setCapacity(room.getCapacity());
		    model.setMax_capacity(room.getMax_capacity());
		    model.setPrice_per_day(room.getPrice_per_day());
		    model.setPrice_per_hour(room.getPrice_per_hour());
		    model.setCalculate_by(room.getCalculate_by());
		    model.setMin_deposit(room.getMin_deposit());
		    model.setType(room.getType());
		    model.setNote(room.getNote());
		    model.setCreate_date(new Date());
		    model.setStatus(1);
		    
		    responseJSON = roomService.createRoom(model);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
	    return ret(responseJSON);
	}
	
	// [UPDATE]
	@PostMapping(path="/edit") // Map ONLY POST Requests
	public @ResponseBody JSONObject updateRoom(@RequestBody Room room) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [0]Initialize
			String id = room.getId();
			String hotelid = room.getHotelid();
			String roomno = room.getRoomno();
			
			// [1] Validation Checking
			if(StringUtils.isBlank(id) && !StringUtil.areNotBlank(hotelid, roomno)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				responseJSON.put("msg", "id OR (roomno + hotelid) needed for identification");
				return responseJSON;
			}
			
			// [3] Modeling
		    Room model = new Room();
		    model.setId(id);//Identification Checking
		    if(StringUtils.isBlank(id)) model.setHotelid(hotelid);//[Option2]Identification Checking
		    if(StringUtils.isBlank(id)) model.setRoomno(roomno);//[Option2]Identification Checking
		    
		    model.setCapacity(room.getCapacity());
		    model.setMax_capacity(room.getMax_capacity());
		    model.setPrice_per_day(room.getPrice_per_day());
		    model.setPrice_per_hour(room.getPrice_per_hour());
		    model.setCalculate_by(room.getCalculate_by());
		    model.setMin_deposit(room.getMin_deposit());
		    model.setType(room.getType());
		    model.setNote(room.getNote());
		    model.setStatus(room.getStatus());
		    
		    responseJSON = roomService.updateRoom(model);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
	    return ret(responseJSON);
	}
	
	// [DELETE]
	@PostMapping(path="/remove")
	public @ResponseBody JSONObject deleteRoom(@RequestBody Room room) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [0]Initialize
			String id = room.getId();
			String hotelid = room.getHotelid();
			String roomno = room.getRoomno();
					
			// [1] Validation Checking
			if(StringUtils.isBlank(id) && !StringUtil.areNotBlank(hotelid, roomno)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				responseJSON.put("msg", "id OR (roomno + hotelid) needed for identification");
				return responseJSON;
			}
			
			responseJSON = roomService.deleteRoom(id, hotelid, roomno);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
	    return ret(responseJSON);
	}
}
