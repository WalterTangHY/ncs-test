package com.example.hotelbooking.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="tb_room_info", schema="Hotel")
public class Room {
	@Id @Column(length=36)
	private String id;
	@NotBlank(message = "roomno is mandatory")
	private String roomno;
	@NotBlank(message = "hotelid is mandatory")
	private String hotelid;
	
	//Threshold
	@Min(1)
	private Integer capacity;
	@Min(1)
	private Integer max_capacity;
	@Min(0)
	private BigDecimal min_deposit;
	
	//Pricing Related
	@Min(0)
	private BigDecimal price_per_day;
	@Min(0)
	private BigDecimal price_per_hour;
	
	private Integer calculate_by;
	
	private Integer type;
	private String note;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date create_date;
	private Integer status;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public Integer getCalculate_by() {
		return calculate_by;
	}
	public void setCalculate_by(Integer calculate_by) {
		this.calculate_by = calculate_by;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
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
	
}
