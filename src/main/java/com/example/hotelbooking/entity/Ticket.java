package com.example.hotelbooking.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="tb_customer_room_ticket", schema="Hotel")
public class Ticket {
	@Id @Column(length=36)
	private String ticketid;
	@NotBlank(message = "customerid is mandatory")
	private String customerid;
	@NotBlank(message = "roomid is mandatory")
	private String roomid;
	@NotBlank(message = "roomno is mandatory")
	private String roomno;
	@NotBlank(message = "hotelid is mandatory")
	private String hotelid;
	
	// Room Snapshot
	private Integer capacity;
	private Integer max_capacity;
	private BigDecimal price_per_day;
	private BigDecimal price_per_hour;
	private BigDecimal min_deposit;
	
	// Promotion Snapshot
	private String promo_code;
	private Integer promo_type;
	private BigDecimal promo_percentage;
	private BigDecimal promo_amount;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date check_in_date;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date check_out_date;
	
	//Calculated Data
	private Integer calculate_unit; //1: Day, 2: Hour
	private Integer calculate_by; //1: Room, 2: Number of People
	private Integer people_num;
	private BigDecimal total_price;
	
	//Given Data
	private BigDecimal deposit;
	private BigDecimal amount_pay;
	
	//Ticket Data
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date create_date;
	private Integer status;
	
	//Audit Data
	private String audit_user;
	private String audit_note;
	private Date audit_date;
	
	public String getTicketid() {
		return ticketid;
	}
	public void setTicketid(String ticketid) {
		this.ticketid = ticketid;
	}
	public String getCustomerid() {
		return customerid;
	}
	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}
	public String getRoomid() {
		return roomid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public String getRoomno() {
		return roomno;
	}
	public void setRoomno(String roomno) {
		this.roomno = roomno;
	}
	public String getHotelid() {
		return hotelid;
	}
	public void setHotelid(String hotelid) {
		this.hotelid = hotelid;
	}
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	public Integer getMax_capacity() {
		return max_capacity;
	}
	public void setMax_capacity(Integer max_capacity) {
		this.max_capacity = max_capacity;
	}
	public BigDecimal getPrice_per_day() {
		return price_per_day;
	}
	public void setPrice_per_day(BigDecimal price_per_day) {
		this.price_per_day = price_per_day;
	}
	public BigDecimal getPrice_per_hour() {
		return price_per_hour;
	}
	public void setPrice_per_hour(BigDecimal price_per_hour) {
		this.price_per_hour = price_per_hour;
	}
	public BigDecimal getMin_deposit() {
		return min_deposit;
	}
	public void setMin_deposit(BigDecimal min_deposit) {
		this.min_deposit = min_deposit;
	}
	public String getPromo_code() {
		return promo_code;
	}
	public void setPromo_code(String promo_code) {
		this.promo_code = promo_code;
	}
	public Integer getPromo_type() {
		return promo_type;
	}
	public void setPromo_type(Integer promo_type) {
		this.promo_type = promo_type;
	}
	public BigDecimal getPromo_percentage() {
		return promo_percentage;
	}
	public void setPromo_percentage(BigDecimal promo_percentage) {
		this.promo_percentage = promo_percentage;
	}
	public BigDecimal getPromo_amount() {
		return promo_amount;
	}
	public void setPromo_amount(BigDecimal promo_amount) {
		this.promo_amount = promo_amount;
	}
	public Date getCheck_in_date() {
		return check_in_date;
	}
	public void setCheck_in_date(Date check_in_date) {
		this.check_in_date = check_in_date;
	}
	public Date getCheck_out_date() {
		return check_out_date;
	}
	public void setCheck_out_date(Date check_out_date) {
		this.check_out_date = check_out_date;
	}
	public Integer getCalculate_unit() {
		return calculate_unit;
	}
	public void setCalculate_unit(Integer calculate_unit) {
		this.calculate_unit = calculate_unit;
	}
	public Integer getCalculate_by() {
		return calculate_by;
	}
	public void setCalculate_by(Integer calculate_by) {
		this.calculate_by = calculate_by;
	}
	public Integer getPeople_num() {
		return people_num;
	}
	public void setPeople_num(Integer people_num) {
		this.people_num = people_num;
	}
	public BigDecimal getTotal_price() {
		return total_price;
	}
	public void setTotal_price(BigDecimal total_price) {
		this.total_price = total_price;
	}
	public BigDecimal getDeposit() {
		return deposit;
	}
	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}
	public BigDecimal getAmount_pay() {
		return amount_pay;
	}
	public void setAmount_pay(BigDecimal amount_pay) {
		this.amount_pay = amount_pay;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getAudit_user() {
		return audit_user;
	}
	public void setAudit_user(String audit_user) {
		this.audit_user = audit_user;
	}
	public String getAudit_note() {
		return audit_note;
	}
	public void setAudit_note(String audit_note) {
		this.audit_note = audit_note;
	}
	public Date getAudit_date() {
		return audit_date;
	}
	public void setAudit_date(Date audit_date) {
		this.audit_date = audit_date;
	}
}
