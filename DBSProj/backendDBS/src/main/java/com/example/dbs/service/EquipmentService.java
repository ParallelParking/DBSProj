package com.example.dbs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dbs.model.BookingEquipment;
import com.example.dbs.model.Equipment;
import com.example.dbs.model.RoomEquipment;
import com.example.dbs.repository.BookingEquipmentRepository; // To catch FK constraints
import com.example.dbs.repository.EquipmentRepository;
import com.example.dbs.repository.RoomEquipmentRepository;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final RoomEquipmentRepository roomEquipmentRepository;
    private final BookingEquipmentRepository bookingEquipmentRepository;

    @Autowired
    public EquipmentService(EquipmentRepository equipmentRepository,
                            RoomEquipmentRepository roomEquipmentRepository,
                            BookingEquipmentRepository bookingEquipmentRepository) {
        this.equipmentRepository = equipmentRepository;
        this.roomEquipmentRepository = roomEquipmentRepository;
        this.bookingEquipmentRepository = bookingEquipmentRepository;

    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    public Optional<Equipment> getEquipmentByType(String type) {
        return equipmentRepository.findById(type);
    }

    public boolean existsByType(String type) {
        return equipmentRepository.existsById(type);
    }

    @Transactional
    public Equipment saveEquipment(Equipment equipment) {
        if (equipment.getType() == null || equipment.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment type cannot be null or empty.");
        }
        
        equipment.setType(equipment.getType().trim().toUpperCase());

        if (existsByType(equipment.getType())) {
            throw new IllegalStateException("Equipment with type '" + equipment.getType() + "' already exists.");
        }
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public void deleteEquipment(String type) {
        if (!existsByType(type)) {
             return; // Already gone or never existed
        }
        // 1. Find RoomEquipment entries with this type and delete them.
        List<RoomEquipment> roomRefs = roomEquipmentRepository.findByType(type);
        roomEquipmentRepository.deleteAllInBatch(roomRefs);
        
        // 2. Find BookingEquipment entries with this type and delete them.
        List<BookingEquipment> bookingRefs = bookingEquipmentRepository.findByEquipmentType(type);
        bookingEquipmentRepository.deleteAllInBatch(bookingRefs);

        try {
            equipmentRepository.deleteById(type);
        } catch (DataIntegrityViolationException e) {
            System.err.println("Attempted to delete equipment type '" + type + "' which is still referenced.");
            // Rethrow a more specific exception or handle it
            throw new DataIntegrityViolationException("Cannot delete equipment type '" + type + "' as it is currently referenced (e.g., in a room or booking).", e);
        }
    }
}
