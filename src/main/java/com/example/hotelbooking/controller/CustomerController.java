package com.example.hotelbooking.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.example.hotelbooking.Constant;
import com.example.hotelbooking.entity.Customer;
import com.example.hotelbooking.entity.Room;
import com.example.hotelbooking.service.CustomerService;

import Utils.StringUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;

@Controller 
@RequestMapping(path="/customer") // This means URL's start with /customer (after Application path)
public class CustomerController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
	
	@Autowired
	private CustomerService customerService;
	
	@PostMapping(path="/login")
	public @ResponseBody JSONObject login(@RequestBody JSONObject jsonObject) {
		String username = jsonObject.getString("username");
		String password = jsonObject.getString("password");
		return ret(customerService.login(username, password));
	}
	
	// [READ]
	@GetMapping(path="")
	public  @ResponseBody JSONObject getCustomer(@RequestParam String id) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			Customer customer = customerService.getCustomerById(id);
			responseJSON.put("code", Constant.SUCCESS_CODE);
			responseJSON.put("data", customer);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
	    return ret(responseJSON);
	}

	@GetMapping(path="/all")
	public @ResponseBody JSONObject getAllCustomers() {
		JSONObject responseJSON = new JSONObject(true);
		try {
			Iterable<Customer> customers = customerService.getCustomers();
			responseJSON.put("code", Constant.SUCCESS_CODE);
			responseJSON.put("data", customers);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
	    return ret(responseJSON);
	}
	
	// [CREATE]
	@PostMapping(path="/add") // Map ONLY POST Requests
	public @ResponseBody JSONObject addNewCustomer (@Valid @RequestBody Customer customer) {
		JSONObject responseJSON = new JSONObject();
		try {
			String id = StringUtil.shortUUIDWithDate();
			String salt = StringUtil.shortUUIDWithDate();
			String hashed_password = StringUtil.encryptPassword(customer.getPassword(), salt);
			
			//Modeling
		    Customer model = new Customer();
		    model.setId(id);
		    model.setName(customer.getName());
		    model.setBirth_date(customer.getBirth_date());
		    model.setUsername(customer.getUsername());
		    model.setPassword(hashed_password);
		    model.setSalt(salt);
		    model.setSession_token(StringUtil.shortUUIDWithDate());
		    model.setPhone_no(customer.getPhone_no());
		    model.setEmail(customer.getEmail());
		    model.setStatus(1);
		    model.setCreate_date(new Date());
		    
		    responseJSON = customerService.saveCustomer(model);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
	    return ret(responseJSON);
	}
	
	// [UPDATE]
	@PostMapping(path="/edit") // Map ONLY POST Requests
	public @ResponseBody JSONObject updateCustomer (@Valid @RequestBody Customer customer) {
		JSONObject responseJSON = new JSONObject(true);
		
		try {
			// [0]Initialize
			String username = customer.getUsername();
			
			// [1] Validation Checking
			if(StringUtils.isBlank(username)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				responseJSON.put("msg", "username & sessionToken needed for verification");
				return responseJSON;
			}
			
			//Modeling
		    Customer model = new Customer();
		    model.setUsername(customer.getUsername());//Identification Checking
		    model.setName(customer.getName());
		    model.setBirth_date(customer.getBirth_date());
		    model.setPhone_no(customer.getPhone_no());
		    model.setEmail(customer.getEmail());
		    
		    responseJSON = customerService.updateCustomer(model);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
	    
	    return ret(responseJSON);
	}
	
	// [DELETE]
	@PostMapping(path="/remove")
	public @ResponseBody JSONObject deleteRoom(@RequestBody Customer customer) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [0]Initialize
			String id = customer.getId();
			String username = customer.getUsername();
					
			// [1] Validation Checking
			if(StringUtils.isBlank(id) && StringUtils.isBlank(username)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				responseJSON.put("msg", "id OR username needed for identification");
				return responseJSON;
			}
			
			responseJSON = customerService.deleteCustomer(id, username);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		   return ret(responseJSON);
	}
}
