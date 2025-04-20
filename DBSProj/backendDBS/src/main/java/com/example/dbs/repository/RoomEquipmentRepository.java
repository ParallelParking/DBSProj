package com.example.dbs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dbs.model.RoomEquipment;
import com.example.dbs.model.RoomEquipmentId;

public interface RoomEquipmentRepository extends JpaRepository<RoomEquipment, RoomEquipmentId> {
    
    List<RoomEquipment> findByType(String type);

    List<RoomEquipment> findByBlockAndRoom(String block, String room);
}
