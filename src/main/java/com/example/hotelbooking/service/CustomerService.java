package com.example.hotelbooking.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.example.hotelbooking.Constant;
import com.example.hotelbooking.controller.CustomerController;
import com.example.hotelbooking.entity.Customer;
import com.example.hotelbooking.repository.CustomerRepository;

import Utils.StringUtil;
import io.micrometer.common.util.StringUtils;

@Service
public class CustomerService {
	private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

	@Autowired
	private CustomerRepository repository;

	public Iterable<Customer> getCustomers() {
		return repository.findAll();
	}

	public Customer getCustomerById(String id) {
		return repository.findById(id).orElse(null);
	}

	public Customer getCustomerByUsername(String username) {
		return repository.findByUsername(username);
	}

	public JSONObject saveCustomer(Customer customer) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [0]Initialize
			String id = customer.getId();
			String name = customer.getName();
			String username = customer.getUsername();
			String password = customer.getPassword();
			String salt = customer.getSalt();
			Integer status = customer.getStatus();
			
			// [1]Validation Check
			//--- NULL CHECK
			if(!StringUtil.areNotBlank(id, name, username, password, salt) || status == null) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			//--- Duplicate Check
			Customer existingCustomer = getCustomerByUsername(username);
			if (existingCustomer != null) {
				responseJSON.put("code", Constant.CUSTOMER_EXISTED_CODE);
				return responseJSON;
			}
			
			// [4] Return Data
			repository.save(customer);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}

		return responseJSON;
	}
	
	public JSONObject updateCustomer(Customer customer) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [0] Initialize
			String username = customer.getUsername(), sessionToken = customer.getSession_token(),
							name = customer.getName(), phoneno = customer.getPhone_no(), email = customer.getEmail();
			Date birthDate = customer.getBirth_date();
			
			// [1] Validation Check
			//--- Existence Check
			Customer existingCustomer = repository.findByUsername(username);
			if (existingCustomer == null) {
				responseJSON.put("code", Constant.CUSTOMER_NOT_EXIST_CODE);
				return responseJSON;
			}

			// [3] Modeling
			if (birthDate != null) existingCustomer.setBirth_date(birthDate);
			if (StringUtils.isNotBlank(email)) existingCustomer.setEmail(email);
			if (StringUtils.isNotBlank(name)) existingCustomer.setName(name);
			if (StringUtils.isNotBlank(phoneno)) existingCustomer.setPhone_no(phoneno);

			// [4] Return Data
			repository.save(existingCustomer);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}

	public JSONObject deleteCustomer(String id, String username) {
		JSONObject responseJSON = new JSONObject(true);
		try {
			// [1] Validation Check
			Customer existingCustomer = new Customer();
			if(StringUtils.isNotBlank(id)) {
				existingCustomer = getCustomerById(id);
			}else if(StringUtils.isNotBlank(username)){
				existingCustomer = repository.findByUsername(username);
			}else {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			//--- Existence Check
			if (existingCustomer == null) {
				responseJSON.put("code", Constant.CUSTOMER_NOT_EXIST_CODE);
				return responseJSON;
			}
			
			// [4] Return Data
			existingCustomer.setStatus(-1);
			repository.save(existingCustomer);
			responseJSON.put("code", Constant.SUCCESS_CODE);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}
		return responseJSON;
	}

	public JSONObject login(String username, String password) {
		JSONObject responseJSON = new JSONObject();
		try {
			// []Validation Check
			//--- NULL Check
			if(!StringUtil.areNotBlank(username, password)) {
				responseJSON.put("code", Constant.BAD_REQUEST_CODE);
				return responseJSON;
			}
			
			//--- Existing User Check
			Customer existingCustomer = getCustomerByUsername(username);
			if (existingCustomer == null) {
				responseJSON.put("code", Constant.AUTHORIZE_FAILED_CODE);
				return responseJSON;
			}
			
			//--- Authentication Check
			boolean isAuthorize = StringUtil.isAuthenticate(password, existingCustomer);
			if (!isAuthorize) {
				responseJSON.put("code", Constant.AUTHORIZE_FAILED_CODE);
				return responseJSON;
			}
			
			// [?] Extra Logic
			String new_sessionToken = StringUtil.shortUUIDWithDate();
			existingCustomer.setSession_token(new_sessionToken);
			
			// [4] Return Data
			updateCustomer(existingCustomer);
			responseJSON.put("code", Constant.SUCCESS_CODE);
			responseJSON.put("sessionId", new_sessionToken);
		}catch(Exception ex) {
			log.error(ex.getMessage());
			responseJSON.put("code", Constant.INTERNAL_SERVER_ERROR);
		}

		return responseJSON;
	}

}
