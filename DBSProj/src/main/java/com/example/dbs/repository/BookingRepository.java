package com.example.dbs.repository;

import com.example.dbs.model.Booking;
import com.example.dbs.model.BookingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, BookingId> {
}
