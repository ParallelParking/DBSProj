package com.example.dbs.repository;

import com.example.dbs.model.BookingApproval;
import com.example.dbs.model.BookingApprovalId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingApprovalRepository extends JpaRepository<BookingApproval, BookingApprovalId> {
}
