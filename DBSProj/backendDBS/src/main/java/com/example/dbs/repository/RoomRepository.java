package com.example.dbs.repository;

import com.example.dbs.model.Room;
import com.example.dbs.model.RoomId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, RoomId> {
}
