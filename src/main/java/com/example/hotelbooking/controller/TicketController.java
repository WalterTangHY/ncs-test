package com.example.hotelbooking.controller;

import java.math.BigDecimal;
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
import com.example.hotelbooking.entity.Ticket;
import com.example.hotelbooking.service.TicketService;

import Utils.StringUtil;
import jakarta.validation.Valid;

@Controller
@RequestMapping(path="/book")
public class TicketController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(TicketController.class);

	@Autowired
	private TicketService ticketService;
	
	// [READ]
	@GetMapping(path="/all")
	public @ResponseBody JSONObject getAllTickets() {
		JSONObject responseJSON = new JSONObject(true);
		try {
			Iterable<Ticket> tickets = ticketService.getAllTicket();
			responseJSON.put("code", Constant.SUCCESS_CODE);
			responseJSON.put("data", tickets);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return ret(responseJSON);
	}
	
	@GetMapping(path="")
	public @ResponseBody JSONObject getTicket(@RequestParam String id){
		JSONObject responseJSON = new JSONObject(true);
		try {
			Ticket ticket = ticketService.getTicketById(id);
			responseJSON.put("code", Constant.SUCCESS_CODE);
			responseJSON.put("data", ticket);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}
	
	// [CREATE]
	@PostMapping(path="/add") // Map ONLY POST Requests
	public @ResponseBody JSONObject createTicket(@RequestBody Ticket ticket) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			//Modeling
			ticket.setTicketid(StringUtil.shortUUIDWithDate());
			ticket.setCreate_date(new Date());
			ticket.setStatus(1);
			   
			responseJSON = ticketService.generateTicket(ticket);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return ret(responseJSON);
	}
	
	@GetMapping(path="/pay")
	public @ResponseBody JSONObject payTicket(@RequestParam String id, @RequestParam BigDecimal amountPay){
		JSONObject responseJSON = new JSONObject(true);
		try {
			responseJSON = ticketService.payTicket(id, amountPay, 1);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}
	
	@GetMapping(path="/fullpay")
	public @ResponseBody JSONObject fullpayTicket(@RequestParam String id, @RequestParam BigDecimal amountPay){
		JSONObject responseJSON = new JSONObject(true);
		try {
			responseJSON = ticketService.payTicket(id, amountPay, 8);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}
	
	@GetMapping(path="/cancel")
	public @ResponseBody JSONObject cancelTicket(@RequestParam String id){
		JSONObject responseJSON = new JSONObject(true);
		try {
			responseJSON = ticketService.cancelTicket(id);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}
}
