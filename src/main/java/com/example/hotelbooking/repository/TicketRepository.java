package com.example.hotelbooking.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.hotelbooking.entity.Ticket;

public interface TicketRepository extends CrudRepository<Ticket, String>{
	Iterable<Ticket> findAllTicketByCustomerid(String customerid);
	
	
}
