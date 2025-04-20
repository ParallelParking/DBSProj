package com.example.dbs.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dbs.model.RoomEquipment;
import com.example.dbs.model.RoomId;
import com.example.dbs.service.EquipmentService;
import com.example.dbs.service.RoomEquipmentService;
import com.example.dbs.service.RoomService;
import com.example.dbs.types.RoomEquipmentRequest;

@RestController
@RequestMapping("/api/roomequipments") 
public class RoomEquipmentController {

    private final RoomEquipmentService roomEquipmentService;
    private final RoomService roomService;
    private final EquipmentService equipmentService;

    @Autowired
    public RoomEquipmentController(RoomEquipmentService roomEquipmentService,
                                   RoomService roomService,
                                   EquipmentService equipmentService) {
        this.roomEquipmentService = roomEquipmentService;
        this.roomService = roomService;
        this.equipmentService = equipmentService;
    }

    @PostMapping
    public ResponseEntity<?> addEquipmentToRoom(@RequestBody RoomEquipmentRequest request) {
        // 1. Basic DTO Validation
        if (request.getBlock() == null || request.getBlock().trim().isEmpty() ||
            request.getRoom() == null || request.getRoom().trim().isEmpty() ||
            request.getType() == null || request.getType().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Block, room, and type are required.\"}");
        }

        // 2. Optional: Early existence checks
        RoomId roomId = new RoomId(request.getBlock(), request.getRoom());
        if (roomService.getRoomById(roomId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("{\"error\": \"Room with ID (Block: " + request.getBlock() + ", Room: " + request.getRoom() + ") not found.\"}");
        }
        // Normalize type for check
        String normalizedType = request.getType().trim().toUpperCase();
        if (equipmentService.getEquipmentByType(normalizedType).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("{\"error\": \"Equipment type '" + normalizedType + "' not found.\"}");
        }

        // 3. Attempt to add via service
        try {
            RoomEquipment createdLink = roomEquipmentService.addEquipmentToRoom(request.getBlock(), request.getRoom(), request.getType());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLink); // 201 Created
        } catch (NoSuchElementException e) { // Should be caught by early checks if performed
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (IllegalStateException e) { // Already exists
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"error\": \"" + e.getMessage() + "\"}"); // 409 Conflict
        } catch (Exception e) {
            System.err.println("Error adding equipment to room: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\": \"An internal error occurred while adding equipment to the room.\"}");
        }
    }

    @DeleteMapping("/block/{block}/room/{room}/type/{type}")
    public ResponseEntity<?> removeEquipmentFromRoom(
            @PathVariable String block,
            @PathVariable String room,
            @PathVariable String type) {

        // 1. Validate path variables
        if (block == null || block.trim().isEmpty() ||
            room == null || room.trim().isEmpty() ||
            type == null || type.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Block, room, and type path variables are required.\"}");
        }

        // 2. Attempt removal via service
        try {
            boolean removed = roomEquipmentService.removeEquipmentFromRoom(block, room, type);
            if (removed) {
                return ResponseEntity.noContent().build(); // 204 No Content
            } else {
                // Service returns false if the combination didn't exist
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
        } catch (Exception e) {
            System.err.println("Error removing equipment '" + type + "' from room (Block: " + block + ", Room: " + room + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\": \"An internal error occurred while removing equipment from the room.\"}");
        }
    }

    @GetMapping("/room/{block}/{room}")
    public ResponseEntity<?> getEquipmentForRoom(@PathVariable String block, @PathVariable String room) {
        if (block == null || block.trim().isEmpty() || room == null || room.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Block and room path variables are required.\"}");
        }

        // Check if room exists
        RoomId roomId = new RoomId(block, room);
        if (roomService.getRoomById(roomId).isEmpty()) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                  .body("{\"error\": \"Room with ID (Block: " + block + ", Room: " + room + ") not found.\"}");
        }

        List<RoomEquipment> equipmentList = roomEquipmentService.getEquipmentByRoom(block, room);
        return ResponseEntity.ok(equipmentList); // Returns list of RoomEquipment objects
    }

    @GetMapping("/equipment/{type}")
    public ResponseEntity<?> getRoomsForEquipment(@PathVariable String type) {
        if (type == null || type.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Type path variable is required.\"}");
        }

        // Check if equipment type exists
        String normalizedType = type.trim().toUpperCase();
        if (equipmentService.getEquipmentByType(normalizedType).isEmpty()) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                  .body("{\"error\": \"Equipment type '" + normalizedType + "' not found.\"}");
        }

        List<RoomEquipment> roomList = roomEquipmentService.getRoomsByEquipment(type);
        return ResponseEntity.ok(roomList); // Returns list of RoomEquipment objects
    }

    @GetMapping
    public List<RoomEquipment> getAllRoomEquipment() {
        return roomEquipmentService.getAllRoomEquipment();
    }

    @GetMapping("/block/{block}/room/{room}/type/{type}")
    public ResponseEntity<RoomEquipment> getRoomEquipment(@PathVariable String block, @PathVariable String room, @PathVariable String type) {
        if (block == null || block.trim().isEmpty() ||
            room == null || room.trim().isEmpty() ||
            type == null || type.trim().isEmpty()) {
        return ResponseEntity.badRequest().build();
        }
        return roomEquipmentService.getRoomEquipmentById(block, room, type)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
