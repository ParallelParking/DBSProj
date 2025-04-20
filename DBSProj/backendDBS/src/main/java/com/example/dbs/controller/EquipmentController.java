package com.example.dbs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dbs.model.Equipment;
import com.example.dbs.service.EquipmentService;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<Equipment> getAllEquipment() {
        return equipmentService.getAllEquipment();
    }

    @GetMapping("/{type}")
    public ResponseEntity<Equipment> getEquipmentByType(@PathVariable String type) {
        if (type == null || type.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return equipmentService.getEquipmentByType(type)
                .map(ResponseEntity::ok) // 200 OK if found
                .orElseGet(() -> ResponseEntity.notFound().build()); // 404 Not Found otherwise
    }

    @PostMapping
    public ResponseEntity<?> createEquipment(@RequestBody Equipment equipment) {
        // 1. Basic validation
        if (equipment == null || equipment.getType() == null || equipment.getType().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Equipment type is required.\"}");
        }

        // 2. Attempt to save via service
        try {
            // Normalize type before saving
            equipment.setType(equipment.getType().trim().toUpperCase());
            Equipment createdEquipment = equipmentService.saveEquipment(equipment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEquipment); // 201 Created
        } catch (IllegalStateException e) { // Catch conflict error from service
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"error\": \"" + e.getMessage() + "\"}"); // 409 Conflict
        } catch (IllegalArgumentException e) { // Catch validation errors from service
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}"); // 400 Bad Request
        } catch (Exception e) {
            System.err.println("Error creating equipment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\": \"An internal error occurred while creating the equipment.\"}"); // 500 Internal Server Error
        }
    }

    // PUT is not needed as 'type' is the PK and only field.

    @DeleteMapping("/{type}")
    public ResponseEntity<?> deleteEquipment(@PathVariable String type) {
        // 1. Validate path variable
        if (type == null || type.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Equipment type must be provided in the path.\"}");
        }

        // 2. Check if it exists before attempting delete
        if (!equipmentService.existsByType(type)) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        // 3. Attempt deletion via service
        try {
            equipmentService.deleteEquipment(type);
            return ResponseEntity.noContent().build(); // 204 No Content on success
        } catch (DataIntegrityViolationException e) {
             System.err.println("Error deleting equipment '" + type + "': " + e.getMessage());
             // Return 409 Conflict as the resource cannot be deleted due to dependencies
             return ResponseEntity.status(HttpStatus.CONFLICT)
                                  .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
             System.err.println("Error deleting equipment '" + type + "': " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\": \"An internal error occurred while deleting the equipment.\"}"); // 500 Internal Server Error
        }
    }
}
