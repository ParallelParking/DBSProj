package com.example.dbs.repository;

import com.example.dbs.model.BookingEquipment;
import com.example.dbs.model.BookingEquipmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingEquipmentRepository extends JpaRepository<BookingEquipment, BookingEquipmentId> {
}
