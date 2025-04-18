package com.example.dbs.repository;

import com.example.dbs.model.RoomEquipment;
import com.example.dbs.model.RoomEquipmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomEquipmentRepository extends JpaRepository<RoomEquipment, RoomEquipmentId> {
}
