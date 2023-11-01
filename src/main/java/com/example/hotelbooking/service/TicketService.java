package com.example.hotelbooking.service;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.example.hotelbooking.Constant;
import com.example.hotelbooking.entity.Customer;
import com.example.hotelbooking.entity.Room;
import com.example.hotelbooking.entity.Ticket;
import com.example.hotelbooking.repository.TicketRepository;

import Utils.StringUtil;

@Service
public class TicketService {
	private static final Logger log = LoggerFactory.getLogger(TicketService.class);
	
	@Autowired
	private TicketRepository repository;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private RoomService roomService;
	
	public Iterable<Ticket> getAllTicket(){
		return repository.findAll();
	}
	
	public Ticket getTicketById(String id) {
		return repository.findById(id).orElse(null);
	}
	
	public boolean checkRoomAvailability(String roomId) {
		return true;
	}
	
	public JSONObject generateTicket(Ticket ticket) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [0]Initialize
			String ticketId = StringUtil.shortUUIDWithDate(),
					customerId = ticket.getCustomerid(),
					roomId = ticket.getRoomid();
			
			// Room Snapshot
			String roomno,hotelid;
			Integer capacity,maxCapacity;
			BigDecimal pricePerDay,pricePerHour,minDeposit;
			
			//TODO: Promo Snapshot
			String promoCode;
			Integer promoType;
			BigDecimal promoPercentage,promoAmount;
			
			// Calculation Related
			Date checkInDate = ticket.getCheck_in_date(),
					checkOutDate = ticket.getCheck_out_date();
			Integer calculateUnit = ticket.getCalculate_unit(), //1[Default]: Per Day, 2: Per Hour 
					peopleNum = ticket.getPeople_num();
			
			//Amount Trace
			BigDecimal deposit = ticket.getDeposit(),
					amountPay = ticket.getAmount_pay();
			
			deposit = deposit != null? deposit : BigDecimal.ZERO;
			
			//Calculation Occur Here---
			BigDecimal totalPrice;
			
			//Ticket Detail
			Date createDate = new Date();
			Integer status = 0;
			
			//Audit
			String audit_user = null;
			String audit_note = null;
			Date audit_date = null;
			
			// [1]Validation Check
			//--- NULL CHECK
			if(!StringUtil.areNotBlank(customerId, roomId) || checkInDate == null || checkOutDate == null 
					|| peopleNum == null || calculateUnit == null) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			//--- Negative Value Check
			if(peopleNum <= 0) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				responseJSON.put("msg", "peopleNum must be equals or more than 1");
				return responseJSON;
			}
			
			//--- Existence Check
			//--- a. Customer Existence Check
			Customer customer = customerService.getCustomerById(customerId);
			if(customer == null) {
				responseJSON.put("code", Constant.CUSTOMER_NOT_EXIST_CODE);
				return responseJSON;
			}
			
			//--- b1. Room Existence Check
			Room room = roomService.getRoomById(roomId);
			if(room == null) {
				responseJSON.put("code", Constant.RECORD_NOT_EXIST_CODE);
				responseJSON.put("msg", "Room are Not Available or Existed");
				return responseJSON;
			}
			
			//TODO: Continue here
			//--- b2. Check Room Availability
			
			// [2] Business Logic
			//--- Room Snapshot
			roomno = room.getRoomno();
			hotelid = room.getHotelid();
			capacity = room.getCapacity();
			maxCapacity = room.getMax_capacity();
			
			Integer calculateBy = room.getCalculate_by(); //1[Default]: Per Room, 2: Per Person
			pricePerDay = room.getPrice_per_day();
			pricePerHour = room.getPrice_per_hour();
			minDeposit = room.getMin_deposit();
			
			//--- Threshold Check
			if(maxCapacity != null && peopleNum > maxCapacity) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				responseJSON.put("msg", "Maximum Number of People Allow: " + maxCapacity);
				return responseJSON;
			}
			
			if(minDeposit != null && deposit.compareTo(minDeposit) < 0) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				responseJSON.put("msg", "Minimum Deposit Required: " + minDeposit);
				return responseJSON;
			}
			
			/**
			 * 	pricePerUnit: pricePerDay / pricePerHour
			 *	unit1: dayNum / hourNum
			 *	unit2: room / peopleNum
			 *
			 *	eg: 
			 *	If the price is indicate to 1 people per day, then
			 *	totalPrice = pricePerDay * dayNum * peopleNum;
			 */
			// Calculation
			BigDecimal pricePerUnit;
			Long unitDifferent = checkOutDate.getTime() - checkInDate.getTime();
			Integer unit1;
			if(calculateUnit != null && calculateUnit.equals(2)) {
				//calculate by hour
				pricePerUnit = pricePerHour;
				unit1 = (int) (unitDifferent / (1000 * 60 * 60));
			}else {
				//calculate by day
				pricePerUnit = pricePerDay;
				unit1 = (int) (unitDifferent / (1000 * 60 * 60 * 24));  
			}
			
			Integer unit2 = 1;
			if(calculateBy != null && calculateBy.equals(2)) {
				//calculate by number of people
				unit2 = peopleNum;
			}else {
				//calculate by room
				unit2 = 1;
			}
			
			BigDecimal _unit1 = new BigDecimal(unit1).abs().add(BigDecimal.ONE); //Add 1 more time unit for correct calculation
			totalPrice = pricePerUnit.multiply(_unit1).multiply(new BigDecimal(unit2));
			
			
			// [3] Modeling
			Ticket model = new Ticket();
			model.setTicketid(ticketId);
			model.setCustomerid(customerId);
			model.setRoomid(roomId);
			model.setRoomno(roomno);
			model.setHotelid(hotelid);
			
			//Room Snapshot
			model.setCapacity(capacity);
			model.setMax_capacity(maxCapacity);
			model.setPrice_per_day(pricePerDay);
			model.setPrice_per_hour(pricePerHour);
			model.setMin_deposit(minDeposit);
			
			//Promotion Snapshot
			model.setPromo_code(null);
			model.setPromo_type(null);
			model.setPromo_percentage(null);
			model.setPromo_amount(null);
			
			//Calculate Data
			model.setCheck_in_date(checkInDate);
			model.setCheck_out_date(checkOutDate);
			model.setCalculate_unit(calculateUnit);
			model.setCalculate_by(calculateBy);
			model.setPeople_num(peopleNum);
			model.setTotal_price(totalPrice);
			
			//Given Data
			model.setDeposit(deposit != null? deposit: BigDecimal.ZERO);
			model.setAmount_pay(amountPay != null? amountPay : BigDecimal.ZERO);
			
			//Ticket Data
			model.setCreate_date(createDate);
			model.setStatus(status);
			
			//Audit Data
			model.setAudit_user(audit_user);
			model.setAudit_note(audit_note);
			model.setAudit_date(audit_date);
			
			// [4] Return Data
			repository.save(model);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}

		return responseJSON;
	}
	
	public JSONObject payTicket(String ticketId, BigDecimal amountPay, Integer status) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [1]Validation Check
			//NULL Check
			if(!StringUtil.areNotBlank(ticketId) || amountPay == null) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			//Existence Check
			Ticket ticket = getTicketById(ticketId);
			if(ticket == null) {
				responseJSON.put("code", Constant.RECORD_NOT_EXIST_CODE);
				return responseJSON;
			}
			
			BigDecimal amountPaid = ticket.getAmount_pay();
			
			// [3]Modeling
			ticket.setAmount_pay(amountPay.add(amountPaid));
			ticket.setStatus(status);
			
			// [4] Return Data
			repository.save(ticket);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}

		return responseJSON;
	}
	
	public JSONObject lockTicket(String ticketId) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [1]Validation Check
			//NULL Check
			if(!StringUtil.areNotBlank(ticketId)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
						
			//Existence Check
			Ticket ticket = getTicketById(ticketId);
			if(ticket == null) {
				responseJSON.put("code", Constant.RECORD_NOT_EXIST_CODE);
				return responseJSON;
			}
			
			ticket.setStatus(1);
			repository.save(ticket);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}

		return responseJSON;
	}
	
	public JSONObject finishTicket(String ticketId) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [1]Validation Check
			//NULL Check
			if(!StringUtil.areNotBlank(ticketId)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			//Existence Check
			Ticket ticket = getTicketById(ticketId);
			if(ticket == null) {
				responseJSON.put("code", Constant.RECORD_NOT_EXIST_CODE);
				return responseJSON;
			}
			
			// [3]Modeling
			ticket.setStatus(8);
			
			// [4] Return Data
			repository.save(ticket);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}

		return responseJSON;
	}
	
	public JSONObject cancelTicket(String ticketId) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [1]Validation Check
			//NULL Check
			if(!StringUtil.areNotBlank(ticketId)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			//Existence Check
			Ticket ticket = getTicketById(ticketId);
			if(ticket == null) {
				responseJSON.put("code", Constant.RECORD_NOT_EXIST_CODE);
				return responseJSON;
			}
			
			Integer status = ticket.getStatus();
			if(!status.equals(0)) {
				responseJSON.put("code", Constant.TICKET_FINALIZED_CODE);
				responseJSON.put("msg", "This ticket unable to cancel");
				return responseJSON;
			}
				
			// [3]Modeling
			ticket.setStatus(8);
			
			// [4] Return Data
			repository.save(ticket);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}

		return responseJSON;
	}
	
}
