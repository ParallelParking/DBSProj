package com.example.dbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dbs.model.Booking;
import com.example.dbs.model.BookingId;

public interface BookingRepository extends JpaRepository<Booking, BookingId> {

    List<Booking> findByBlockAndRoomNo(String block, String room);

}
