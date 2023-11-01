package com.example.hotelbooking.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.hotelbooking.entity.Hotel;

@Repository
public interface HotelRepository  extends CrudRepository<Hotel, String> {
	Hotel findByName(String name);
}
