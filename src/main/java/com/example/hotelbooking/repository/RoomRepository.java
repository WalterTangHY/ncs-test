package com.example.hotelbooking.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.example.hotelbooking.entity.Room;

@Repository
public interface RoomRepository extends CrudRepository<Room, String> {
	Iterable<Room> findAllByHotelid(String hotelid);
	Iterable<Room> findAllByHotelidAndStatus(String hotelid, Integer status);
	
	Room findByHotelidAndRoomno(String hotelid, String roomno);
	Room findByIdAndStatus(String id, Integer status);
}
