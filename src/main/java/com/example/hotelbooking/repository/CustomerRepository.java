/**
 * 
 */
package com.example.hotelbooking.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.hotelbooking.entity.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, String> {
	Customer findByUsername(String username);
}
