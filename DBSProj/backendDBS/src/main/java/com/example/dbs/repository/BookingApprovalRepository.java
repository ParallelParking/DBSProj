package com.example.dbs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Import Query
import org.springframework.data.repository.query.Param; // Import Param

import com.example.dbs.model.Booking;
import com.example.dbs.model.BookingApproval;
import com.example.dbs.types.ApproverRole;

public interface BookingApprovalRepository extends JpaRepository<BookingApproval, Long> {

    List<BookingApproval> findByBooking(Booking booking);

    // TO JAWAD: THIS IS A FUCKING MESS AND I DONT KNOW WHAT IT MEANS. CHECK THOROUGHLY

    @Query("SELECT ba FROM BookingApproval ba WHERE ba.block = :block AND ba.roomNo = :roomNo AND ba.startTime = :startTime")
    List<BookingApproval> findByBookingId(@Param("block") String block, @Param("roomNo") String roomNo, @Param("startTime") java.time.LocalDateTime startTime);

    // Optional: Find specific approval for a booking and role (useful for checking duplicates)
     Optional<BookingApproval> findByBookingAndApproverRole(Booking booking, ApproverRole approverRole);

    // Optional: Find specific approval by booking components and role
    Optional<BookingApproval> findByBlockAndRoomNoAndStartTimeAndApproverRole(String block, String roomNo, java.time.LocalDateTime startTime, ApproverRole approverRole);

    // Find if a specific user has already approved/rejected for a role
     Optional<BookingApproval> findByBlockAndRoomNoAndStartTimeAndApproverEmailAndApproverRole(
         String block, String roomNo, java.time.LocalDateTime startTime, String approverEmail, ApproverRole approverRole
     );
}
