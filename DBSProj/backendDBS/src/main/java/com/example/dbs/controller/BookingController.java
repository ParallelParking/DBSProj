package com.example.dbs.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping; // For cleaner error responses
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.dbs.model.Booking;
import com.example.dbs.model.BookingEquipment;
import com.example.dbs.model.BookingId;
import com.example.dbs.service.BookingService;
import com.example.dbs.service.EquipmentService;
import com.example.dbs.types.ApprovalRequest;
import com.example.dbs.types.ApprovalStatus;
import com.example.dbs.types.BookingRequest;

@RestController
@RequestMapping("/api/bookings") // Base path for booking-related endpoints
public class BookingController {

    private final BookingService bookingService;
    @Autowired private EquipmentService equipmentService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // GET /api/bookings - Retrieve all bookings
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // GET /api/bookings/{block}/{roomNo}/{dateTime} - Retrieve a single booking by composite ID
    @GetMapping("/{block}/{roomNo}/{dateTime}")
    public ResponseEntity<Booking> getBookingById(
            @PathVariable String block,
            @PathVariable String roomNo,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {

        BookingId id = bookingService.createBookingId(block, roomNo, dateTime);
        Optional<Booking> bookingOptional = bookingService.getBookingById(id);

        // If using LAZY fetch for BookingEquipment in Booking entity, it won't be loaded here.
        // If needed, either change to EAGER (potentially inefficient) or use a DTO
        // and populate it manually in the service/controller after fetching.

        return bookingOptional.map(ResponseEntity::ok)
                              .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/bookings - Create a new booking
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) { // Use BookingRequest DTO
        try {
            // Basic input validation
            if (request.getBlock() == null || request.getBlock().trim().isEmpty() ||
                request.getRoomNo() == null || request.getRoomNo().trim().isEmpty() ||
                request.getDateTime() == null ||
                request.getStudentEmail() == null || request.getStudentEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\": \"Missing required fields: block, roomNo, dateTime, studentEmail.\"}");
            }
            // Add basic check for equipment list if needed, e.g., non-empty strings
            if (request.getRequiredEquipmentTypes() != null) {
                for (String type : request.getRequiredEquipmentTypes()) {
                    if (type == null || type.trim().isEmpty()) {
                        return ResponseEntity.badRequest().body("{\"error\": \"Equipment types cannot be null or empty.\"}");
                    }
                }
            }

            // Call the service method
            Booking createdBooking = bookingService.createBooking(request);

            // returning the created Booking object.
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking); // 201 Created

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Handle validation errors from the service (room/student/club/equipment not found, conflict)
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}"); // 400 Bad Request
        } catch (Exception e) {
            // Generic internal server error
            System.err.println("Error creating booking: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\": \"An internal error occurred while creating the booking.\"}");
        }
    }

    // PUT /api/bookings/{block}/{roomNo}/{dateTime}/purpose - Update booking purpose (example update)
    @PutMapping("/{block}/{roomNo}/{dateTime}/purpose")
    public ResponseEntity<?> updateBookingPurpose(
            @PathVariable String block,
            @PathVariable String roomNo,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestBody String newPurpose) { // Assuming the body just contains the new purpose string

        BookingId id = bookingService.createBookingId(block, roomNo, dateTime);
        try {
            Optional<Booking> updatedBooking = bookingService.updateBookingPurpose(id, newPurpose);
            return updatedBooking.map(ResponseEntity::ok) // 200 OK with updated booking
                                 .orElseGet(() -> ResponseEntity.notFound().build()); // 404 if not found
        } catch (Exception e) {
             System.err.println("Error updating booking purpose: " + e.getMessage()); // Replace with proper logging
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                  .body("An internal error occurred while updating the booking purpose.");
        }
    }


    // DELETE /api/bookings/{block}/{roomNo}/{dateTime} - Delete a booking by composite ID
    @DeleteMapping("/{block}/{roomNo}/{dateTime}")
    public ResponseEntity<Void> deleteBooking(
            @PathVariable String block,
            @PathVariable String roomNo,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {

        BookingId id = bookingService.createBookingId(block, roomNo, dateTime);
        boolean deleted = bookingService.deleteBooking(id);

        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content on success
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found if booking didn't exist
        }
    }

    @PostMapping("/{block}/{roomNo}/{dateTime}/approvals")
    public ResponseEntity<?> submitApproval(
            @PathVariable String block,
            @PathVariable String roomNo,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestBody ApprovalRequest approvalRequest) { // Use the DTO

        BookingId bookingId = bookingService.createBookingId(block, roomNo, dateTime);

        String approverEmail = approvalRequest.getApproverEmail();
        if (approverEmail == null || approverEmail.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Approver email is required in the request body.");
        }

        ApprovalStatus status;
        try {
            // Ensure ApprovalStatus enum exists in com.example.dbs.types
            status = ApprovalStatus.valueOf(approvalRequest.getStatus().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest().body("Invalid approval status. Must be 'APPROVED' or 'REJECTED'.");
        }

        // manual checks (determining role based on email, checking booking status, etc.)
        try {
            Booking updatedBooking = bookingService.recordApproval(
                    bookingId,
                    approverEmail, // Pass the manually provided email
                    status,
                    approvalRequest.getComments()
            );
            // Return the booking, potentially with updated overallStatus
            return ResponseEntity.ok(updatedBooking);
        } catch (NoSuchElementException e) {
            // Booking not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            // Business logic errors (e.g., already approved/rejected, not pending)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (SecurityException e) {
            // If the service layer performs role validation based on the provided email
            // and determines the user is not authorized for this booking/role.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (Exception e) {
            // Generic internal error
            System.err.println("Error submitting approval: " + e.getMessage()); // Replace logging
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An internal error occurred while submitting approval.", e);
        }
    }

    @PostMapping("/{block}/{roomNo}/{dateTime}/equipment")
    public ResponseEntity<?> addEquipmentToBooking(
            @PathVariable String block,
            @PathVariable String roomNo,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestBody Map<String, String> payload) { // Expect {"equipmentType": "TYPE_NAME"}

        BookingId bookingId = bookingService.createBookingId(block, roomNo, dateTime);
        String equipmentType = payload.get("equipmentType");

        // 1. Validate Input
        if (equipmentType == null || equipmentType.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"equipmentType is required in the request body.\"}");
        }
        String normalizedType = equipmentType.trim().toUpperCase(); // Normalize early

        // 2. Optional: Early checks for booking/equipment existence
        if (bookingService.getBookingById(bookingId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Booking not found.\"}");
        }
        if (equipmentService.getEquipmentByType(normalizedType).isEmpty()) { // Use injected EquipmentService
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Equipment type '" + normalizedType + "' not found.\"}");
        }

        // 3. Call Service
        try {
            BookingEquipment createdLink = bookingService.addEquipmentToBooking(bookingId, normalizedType); // Use normalized type
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLink); // 201 Created

        } catch (NoSuchElementException e) { // Should be caught by early checks if performed
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (IllegalStateException e) { // Already exists
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"error\": \"" + e.getMessage() + "\"}"); // 409 Conflict
        } catch (Exception e) {
             System.err.println("Error adding equipment to booking: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                  .body("{\"error\": \"An internal error occurred.\"}");
        }
    }

    @DeleteMapping("/{block}/{roomNo}/{dateTime}/equipment/{type}")
    public ResponseEntity<?> removeEquipmentFromBooking(
            @PathVariable String block,
            @PathVariable String roomNo,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @PathVariable String type) {

        BookingId bookingId = bookingService.createBookingId(block, roomNo, dateTime);

        // 1. Validate Input
        if (type == null || type.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Equipment type must be provided in the path.\"}");
        }
        // Check if booking exists first
        if (bookingService.getBookingById(bookingId).isEmpty()) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Booking not found.\"}");
        }

        // 2. Call Service
        try {
            boolean removed = bookingService.removeEquipmentFromBooking(bookingId, type);
            if (removed) {
                return ResponseEntity.noContent().build(); // 204 No Content
            } else {
                // Equipment was not associated with this booking
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
        } catch (NoSuchElementException e) { // If booking check is added to service
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            System.err.println("Error removing equipment from booking: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\": \"An internal error occurred.\"}");
        }
    }
}
