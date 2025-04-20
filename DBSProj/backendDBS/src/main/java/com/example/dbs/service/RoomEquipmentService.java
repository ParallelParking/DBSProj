package com.example.dbs.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dbs.model.Equipment;
import com.example.dbs.model.Room;
import com.example.dbs.model.RoomEquipment;
import com.example.dbs.model.RoomEquipmentId;
import com.example.dbs.model.RoomId;
import com.example.dbs.repository.EquipmentRepository;
import com.example.dbs.repository.RoomEquipmentRepository;
import com.example.dbs.repository.RoomRepository;

@Service
public class RoomEquipmentService {

    private final RoomEquipmentRepository roomEquipmentRepository;
    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;

    @Autowired
    public RoomEquipmentService(RoomEquipmentRepository roomEquipmentRepository,
                                RoomRepository roomRepository,
                                EquipmentRepository equipmentRepository) {
        this.roomEquipmentRepository = roomEquipmentRepository;
        this.roomRepository = roomRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Transactional
    public RoomEquipment addEquipmentToRoom(String block, String roomIdentifier, String equipmentType) {
        // 1. Validate Room exists
        RoomId roomId = new RoomId(block, roomIdentifier);
        if (!roomRepository.existsById(roomId)) {
            throw new NoSuchElementException("Room with ID (Block: " + block + ", Room: " + roomIdentifier + ") not found.");
        }

        // 2. Validate Equipment exists
        String normalizedEquipmentType = equipmentType.trim().toUpperCase();
        if (!equipmentRepository.existsById(normalizedEquipmentType)) {
            throw new NoSuchElementException("Equipment type '" + normalizedEquipmentType + "' not found.");
        }

        // 3. Check if this equipment is already in the room
        RoomEquipmentId id = new RoomEquipmentId(block, roomIdentifier, normalizedEquipmentType);
        if (roomEquipmentRepository.existsById(id)) {
            throw new IllegalStateException("Equipment type '" + normalizedEquipmentType + "' is already present in Room (Block: " + block + ", Room: " + roomIdentifier + ").");
        }

        // 4. Create and save the relationship
        RoomEquipment roomEquipment = new RoomEquipment();
        roomEquipment.setBlock(block);
        roomEquipment.setRoom(roomIdentifier);
        roomEquipment.setType(normalizedEquipmentType);
 
        Room roomRef = roomRepository.findById(roomId).orElseThrow();
        Equipment equipRef = equipmentRepository.findById(normalizedEquipmentType).orElseThrow();
        roomEquipment.setRoomRef(roomRef);
        roomEquipment.setEquipment(equipRef);

        return roomEquipmentRepository.save(roomEquipment);
    }

    @Transactional
    public boolean removeEquipmentFromRoom(String block, String roomIdentifier, String equipmentType) {
        // Normalize type
        String normalizedEquipmentType = equipmentType.trim().toUpperCase();
        RoomEquipmentId id = new RoomEquipmentId(block, roomIdentifier, normalizedEquipmentType);

        if (roomEquipmentRepository.existsById(id)) {
            roomEquipmentRepository.deleteById(id);
            return true;
        }
        return false; // Equipment was not in the room
    }

    public Optional<RoomEquipment> getRoomEquipmentById(String block, String roomIdentifier, String equipmentType) {
         String normalizedEquipmentType = equipmentType.trim().toUpperCase();
         RoomEquipmentId id = new RoomEquipmentId(block, roomIdentifier, normalizedEquipmentType);
         return roomEquipmentRepository.findById(id);
    }

    public List<RoomEquipment> getEquipmentByRoom(String block, String roomIdentifier) {
        return roomEquipmentRepository.findByBlockAndRoom(block, roomIdentifier);
    }

    public List<RoomEquipment> getRoomsByEquipment(String equipmentType) {
        String normalizedEquipmentType = equipmentType.trim().toUpperCase();
        return roomEquipmentRepository.findByType(normalizedEquipmentType);
    }

    public List<RoomEquipment> getAllRoomEquipment() {
        return roomEquipmentRepository.findAll();
    }
}
