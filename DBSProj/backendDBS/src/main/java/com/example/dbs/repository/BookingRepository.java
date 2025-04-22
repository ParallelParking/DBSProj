package com.example.dbs.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.dbs.model.Booking;
import com.example.dbs.model.BookingId;

public interface BookingRepository extends JpaRepository<Booking, BookingId> {

    List<Booking> findByBlockAndRoomNo(String block, String room);

    // (Existing Start Time < New End Time) AND (Existing End Time > New Start Time)
    // We also exclude bookings that are CANCELLED or REJECTED.
    @Query("SELECT b FROM Booking b " +
           "WHERE b.id.block = :block " +
           "AND b.id.roomNo = :roomNo " +
           "AND b.startTime < :newEndTime " +
           "AND b.endTime > :newStartTime " +
           "AND b.overallStatus NOT IN (com.example.dbs.types.BookingStatus.CANCELLED, com.example.dbs.types.BookingStatus.REJECTED)")
    List<Booking> findConflictingBookings(
            @Param("block") String block,
            @Param("roomNo") String roomNo,
            @Param("newStartTime") LocalDateTime newStartTime,
            @Param("newEndTime") LocalDateTime newEndTime);
}
