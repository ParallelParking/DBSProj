package com.example.dbs.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dbs.model.BookingEquipment;
import com.example.dbs.model.BookingEquipmentId;

public interface BookingEquipmentRepository extends JpaRepository<BookingEquipment, BookingEquipmentId> {
    
    List<BookingEquipment> findByEquipmentType(String equipmentType);

    List<BookingEquipment> findByBlockAndRoomAndDateTime(String block, String room, LocalDateTime dateTime);
}
