package com.example.hotelbooking.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.example.hotelbooking.Constant;
import com.example.hotelbooking.entity.Hotel;
import com.example.hotelbooking.entity.Room;
import com.example.hotelbooking.repository.RoomRepository;

import Utils.StringUtil;
import io.micrometer.common.util.StringUtils;

@Service
public class RoomService {
	private static final Logger log = LoggerFactory.getLogger(RoomService.class);
	
	@Autowired
	private RoomRepository repository;
	
	@Autowired
	private HotelService hotelService;
	
	public Iterable<Room> getAllRooms(String hotelid){
		return repository.findAllByHotelid(hotelid);
	}
	
	public Iterable<Room> getAvailableRooms(String hotelid){
		return repository.findAllByHotelidAndStatus(hotelid, 1);
	}
	
	public Room getRoomById(String id) {
		return repository.findByIdAndStatus(id, 1);
	}
	
	public Room getRoom(String hotelid, String roomno) {
		return repository.findByHotelidAndRoomno(hotelid, roomno);
	}
	
	public JSONObject createRoom(Room room) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [0]Initialize
			String id = room.getId();
			String roomno = room.getRoomno();
			String hotelid = room.getHotelid();
			BigDecimal price_per_day = room.getPrice_per_day();
			BigDecimal price_per_hour = room.getPrice_per_hour();
			Integer calculate_by = room.getCalculate_by();
			Integer status = room.getStatus();
			
			// [1] Validation Check
			//--- NULL Check
			if(!StringUtil.areNotBlank(id, roomno, hotelid) || price_per_day == null || price_per_hour == null || calculate_by == null || status == null) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			//--- Dependencies Existence Check (Hotel)
			Hotel hotel = hotelService.getHotelById(hotelid);
			if(hotel == null) {
				responseJSON.put("code", Constant.RECORD_NOT_EXIST_CODE);
				responseJSON.put("msg", "Hotel Not Existed");
				return responseJSON;
			}
			
			//--- Duplicate Check
			Room existingRoom = repository.findByHotelidAndRoomno(hotelid, roomno);
			if(existingRoom != null) {
				responseJSON.put("code", Constant.RECORD_EXISTED_CODE);
				return responseJSON;
			}
			
			// [4] Return Data
			repository.save(room);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		
		return responseJSON;
	}
	
	public JSONObject updateRoom(Room room) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [0] Initialize
			String id = room.getId();
			String hotelid = room.getHotelid(); //[Option2]
			String roomno = room.getRoomno(); //[Option2]
			
			//Threshold
			Integer capacity = room.getCapacity();
			Integer max_capacity = room.getMax_capacity();
			BigDecimal min_deposit = room.getMin_deposit();
			
			//Pricing Related
			BigDecimal price_per_day = room.getPrice_per_day();
			BigDecimal price_per_hour = room.getPrice_per_hour();
			Integer calculate_by = room.getCalculate_by();
			
			//Room Description
			Integer type = room.getType();
			String note = room.getNote();
			Integer status = room.getStatus();
			
			// [1] Validation Check
			Room existingRoom = new Room();
			if(StringUtils.isNotBlank(id)) {
				existingRoom = getRoomById(id);
			}else if(StringUtil.areNotBlank(hotelid, roomno)){
				existingRoom = repository.findByHotelidAndRoomno(hotelid, roomno);
			}
			
			//--- Existence Check
			if(existingRoom == null) {
				responseJSON.put("code", Constant.RECORD_NOT_EXIST_CODE);
				return responseJSON;
			}
			
			// [3] Modeling
			if(capacity != null) existingRoom.setCapacity(capacity);
			if(max_capacity!=null) existingRoom.setMax_capacity(max_capacity);
			if(min_deposit!=null) existingRoom.setMin_deposit(min_deposit);
			if(price_per_day!=null) existingRoom.setPrice_per_day(price_per_day);
			if(price_per_hour!=null) existingRoom.setPrice_per_hour(price_per_hour);
			if(calculate_by!=null) existingRoom.setCalculate_by(calculate_by);
			if(type!=null) existingRoom.setType(type);
			if(StringUtils.isNotBlank(note)) existingRoom.setNote(note);
			if(status!=null) existingRoom.setStatus(status);
			
			// [4] Return Data
			repository.save(existingRoom);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}
	
	public JSONObject deleteRoom(String id, String hotelid, String roomno) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [1] Validation Check
			Room existingRoom = new Room();
			if(StringUtils.isNotBlank(id)) {
				existingRoom = getRoomById(id);
			}else if(StringUtil.areNotBlank(hotelid, roomno)){
				existingRoom = repository.findByHotelidAndRoomno(hotelid, roomno);
			}else {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			//--- Existence Check
			if(existingRoom == null) {
				responseJSON.put("code", Constant.RECORD_NOT_EXIST_CODE);
				return responseJSON;
			}
			
			// [4] Return Data
			existingRoom.setStatus(-1);
			repository.save(existingRoom);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}
	
	public JSONObject lockRoom(String id) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [1] Validation Check
			if(StringUtils.isBlank(id)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			Room existingRoom =  getRoomById(id);
			//--- Existence Check
			if(existingRoom == null) {
				responseJSON.put("code", Constant.RECORD_NOT_EXIST_CODE);
				return responseJSON;
			}
			
			// [4] Return Data
			existingRoom.setStatus(0);//Under Maintenance
			repository.save(existingRoom);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}
	
	public JSONObject unlockRoom(String id) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [1] Validation Check
			if(StringUtils.isBlank(id)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			Room existingRoom =  getRoomById(id);
			//--- Existence Check
			if(existingRoom == null) {
				responseJSON.put("code", Constant.RECORD_NOT_EXIST_CODE);
				return responseJSON;
			}
			
			// [4] Return Data
			existingRoom.setStatus(1);
			repository.save(existingRoom);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}
}
