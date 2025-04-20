package com.example.dbs.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dbs.model.Booking;
import com.example.dbs.model.BookingApproval;
import com.example.dbs.model.BookingEquipment;
import com.example.dbs.model.BookingEquipmentId;
import com.example.dbs.model.BookingId;
import com.example.dbs.model.Equipment;
import com.example.dbs.model.Professor;
import com.example.dbs.model.Room;
import com.example.dbs.model.RoomId;
import com.example.dbs.repository.BookingApprovalRepository; // For atomic operations
import com.example.dbs.repository.BookingEquipmentRepository;
import com.example.dbs.repository.BookingRepository;
import com.example.dbs.repository.ClubRepository;
import com.example.dbs.repository.EquipmentRepository;
import com.example.dbs.repository.FloorManagerRepository;
import com.example.dbs.repository.ProfessorRepository;
import com.example.dbs.repository.RoomRepository;
import com.example.dbs.repository.SecurityRepository;
import com.example.dbs.repository.StudentCouncilRepository;
import com.example.dbs.repository.StudentRepository;
import com.example.dbs.types.ApprovalStatus;
import com.example.dbs.types.ApproverRole;
import com.example.dbs.types.BookingRequest;
import com.example.dbs.types.BookingStatus;

@Service
public class BookingService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private BookingApprovalRepository bookingApprovalRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private ClubRepository clubRepository;
    @Autowired private ProfessorRepository professorRepository;
    @Autowired private StudentCouncilRepository studentCouncilRepository;
    @Autowired private FloorManagerRepository floorManagerRepository;
    @Autowired private SecurityRepository securityRepository;
    @Autowired private EquipmentRepository equipmentRepository;
    @Autowired private BookingEquipmentRepository bookingEquipmentRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(BookingId id) {
        return bookingRepository.findById(id);
    }

    // Helper to create BookingId easily
    public BookingId createBookingId(String block, String roomNo, LocalDateTime dateTime) {
        return new BookingId(block, roomNo, dateTime);
    }

    @Transactional
    public Booking createBooking(BookingRequest request) { // Accept DTO
        // 1. Validate Room, Student, Club (if provided)
        if (!roomRepository.existsById(new RoomId(request.getBlock(), request.getRoomNo()))) {
             throw new IllegalArgumentException("Room does not exist.");
        }
        if (!studentRepository.existsById(request.getStudentEmail())) {
              throw new IllegalArgumentException("Student does not exist.");
        }
        if (request.getClubName() != null && !request.getClubName().trim().isEmpty() && !clubRepository.existsById(request.getClubName())) {
             throw new IllegalArgumentException("Club does not exist.");
        }

        // 2. Check for booking conflict
        BookingId bookingId = new BookingId(request.getBlock(), request.getRoomNo(), request.getDateTime());
        if (bookingRepository.existsById(bookingId)) {
             throw new IllegalStateException("Booking conflict exists for this room and time.");
        }

        // 3. Validate Required Equipment (if provided)
        List<String> requiredEquipmentTypes = request.getRequiredEquipmentTypes();
        if (requiredEquipmentTypes != null && !requiredEquipmentTypes.isEmpty()) {
            for (String type : requiredEquipmentTypes) {
                 // Normalize type for consistency
                 String normalizedType = type.trim().toUpperCase();
                 if (!equipmentRepository.existsById(normalizedType)) {
                     throw new IllegalArgumentException("Required equipment type '" + normalizedType + "' does not exist.");
                 }
            }
        }

        // 4. Create and Save the Booking entity first
        Booking newBooking = new Booking();
        newBooking.setBlock(request.getBlock());
        newBooking.setRoomNo(request.getRoomNo());
        newBooking.setDateTime(request.getDateTime());
        newBooking.setPurpose(request.getPurpose());
        newBooking.setStudentEmail(request.getStudentEmail());
        newBooking.setClubName(request.getClubName()); // Can be null
        newBooking.setOverallStatus(BookingStatus.PENDING_APPROVAL);

        Booking savedBooking = bookingRepository.save(newBooking);

        // 5. Create and Save BookingEquipment entities
        if (requiredEquipmentTypes != null && !requiredEquipmentTypes.isEmpty()) {
            List<BookingEquipment> bookingEquipments = new ArrayList<>();
            for (String type : requiredEquipmentTypes) {
                 String normalizedType = type.trim().toUpperCase(); // Use normalized type
                 BookingEquipment be = new BookingEquipment();
                 be.setBlock(savedBooking.getBlock());
                 be.setRoom(savedBooking.getRoomNo()); // Match BookingEquipment field name 'room'
                 be.setDateTime(savedBooking.getDateTime());
                 be.setEquipmentType(normalizedType);
                 // Set the relationship back to the booking (optional but good practice)
                 // Fetching equipment again is inefficient, we already validated it
                 Equipment equipRef = equipmentRepository.findById(normalizedType).orElseThrow(); // Should exist
                 be.setEquipment(equipRef);
                 be.setBooking(savedBooking);
                 bookingEquipments.add(be);
            }
            bookingEquipmentRepository.saveAll(bookingEquipments);
        }

        return savedBooking; // Return the created booking
    }


    @Transactional
    public Booking recordApproval(BookingId bookingId, String approverEmail, ApprovalStatus status, String comments) {
        // 1. Find the booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));

        // 2. Check if booking is still pending
        if (booking.getOverallStatus() != BookingStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Booking is not pending approval (Status: " + booking.getOverallStatus() + ")");
        }

        ApproverRole role = determineApproverRole(approverEmail, booking);
        if (role == null) {
            throw new SecurityException("User " + approverEmail + " is not an authorized approver for this booking.");
        }

        // 4. Check if this user/role combination already submitted an approval
        Optional<BookingApproval> existingApproval = bookingApprovalRepository.findByBookingAndApproverRole(booking, role);
        if (existingApproval.isPresent()) {
              throw new IllegalStateException("Approval for role " + role + " has already been submitted for this booking.");
        }

        // 5. Create and save the new approval record
        BookingApproval approval = new BookingApproval(
                booking,
                role,
                approverEmail,
                status,
                LocalDateTime.now(),
                comments
        );
        bookingApprovalRepository.save(approval);

        // 6. Update the overall booking status based on all approvals
        updateOverallBookingStatus(booking);

        // Return the updated booking (might have new overallStatus)
        return booking;
    }

    private ApproverRole determineApproverRole(String approverEmail, Booking booking) {
        // Check if Floor Manager for this Room
        Room room = roomRepository.findById(new RoomId(booking.getBlock(), booking.getRoomNo())).orElse(null);
        if (room != null && room.getManager() != null && room.getManager().getEmail().equals(approverEmail)) {
            return ApproverRole.FLOOR_MANAGER;
        }

        // Check if Faculty Head for this Club
        if (booking.getClub() != null && booking.getClub().getFacultyHead() != null && booking.getClub().getFacultyHeadEmail().equals(approverEmail)) {
            return ApproverRole.FACULTY_HEAD;
        }

        // Check if Cultural Professor
        Optional<Professor> profOpt = professorRepository.findById(approverEmail);
        if (profOpt.isPresent() && Boolean.TRUE.equals(profOpt.get().getIsCultural())) {
            return ApproverRole.CULTURAL_PROF;
        }

        // Check if Student Council
        if (studentCouncilRepository.existsById(approverEmail)) {
            return ApproverRole.STUDENT_COUNCIL;
        }

        // Check if Security
        if (securityRepository.existsById(approverEmail)) {
            return ApproverRole.SECURITY;
        }

        if (floorManagerRepository.existsById(approverEmail)) {
            return ApproverRole.FLOOR_MANAGER;
        }

        return null; // Not a recognized approver for this booking context
    }

    @Transactional // Should be part of the recordApproval transaction
    protected void updateOverallBookingStatus(Booking booking) {
        // Reload booking to ensure we have the latest state within the transaction
        booking = bookingRepository.findById(new BookingId(booking.getBlock(), booking.getRoomNo(), booking.getDateTime())).orElseThrow();

        // Find all approvals for this booking
        List<BookingApproval> approvals = bookingApprovalRepository.findByBooking(booking);

        // Check for any rejection first
        boolean rejected = approvals.stream()
                .anyMatch(a -> a.getApprovalStatus() == ApprovalStatus.REJECTED);

        if (rejected) {
            booking.setOverallStatus(BookingStatus.REJECTED);
            bookingRepository.save(booking);
            return; // No need to check further
        }

        // Get the set of roles that have approved
        Set<ApproverRole> approvedRoles = approvals.stream()
                .filter(a -> a.getApprovalStatus() == ApprovalStatus.APPROVED)
                .map(BookingApproval::getApproverRole)
                .collect(Collectors.toSet());

        Set<ApproverRole> requiredRoles = EnumSet.of(
                ApproverRole.FACULTY_HEAD,
                ApproverRole.STUDENT_COUNCIL,
                ApproverRole.CULTURAL_PROF,
                ApproverRole.FLOOR_MANAGER,
                ApproverRole.SECURITY
        );

        if (booking.getClubName() == null || booking.getClubName().isEmpty()) {
              requiredRoles.remove(ApproverRole.FACULTY_HEAD); // Example: No faculty head needed if not a club booking
        }

        // Check if all required roles are present in the approved set
        if (approvedRoles.containsAll(requiredRoles)) {
            booking.setOverallStatus(BookingStatus.APPROVED);
            bookingRepository.save(booking);
        } else {
            booking.setOverallStatus(BookingStatus.PENDING_APPROVAL);
            bookingRepository.save(booking); // Save only if state actually changes
        }
    }

    @Transactional
    public Optional<Booking> updateBookingPurpose(BookingId id, String newPurpose) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setPurpose(newPurpose);
            return Optional.of(bookingRepository.save(booking));
        }
        return Optional.empty();
    }

    @Transactional
    public boolean deleteBooking(BookingId id) {
        if (bookingRepository.existsById(id)) {
            // 1. Delete associated approvals (handled by cascade on Booking entity)

            // 2. Explicitly delete associated BookingEquipment
            List<BookingEquipment> equipmentToDelete = bookingEquipmentRepository.findByBlockAndRoomAndDateTime(
                    id.getBlock(), id.getRoomNo(), id.getDateTime());
            if (!equipmentToDelete.isEmpty()) {
                bookingEquipmentRepository.deleteAllInBatch(equipmentToDelete);
            }

            // 3. Delete the booking itself
            bookingRepository.deleteById(id);
            return true;
        }
        return false; // Booking not found
    }

    @Transactional
    public BookingEquipment addEquipmentToBooking(BookingId bookingId, String equipmentType) {
        // 1. Validate Booking exists
        if (!bookingRepository.existsById(bookingId)) {
            throw new NoSuchElementException("Booking with ID " + bookingId + " not found.");
        }

        // 2. Validate Equipment exists (and normalize)
        String normalizedType = equipmentType.trim().toUpperCase();
        if (!equipmentRepository.existsById(normalizedType)) {
            throw new NoSuchElementException("Equipment type '" + normalizedType + "' not found.");
        }

        // 3. Check if already exists
        BookingEquipmentId beId = new BookingEquipmentId(bookingId.getBlock(), bookingId.getRoomNo(), bookingId.getDateTime(), normalizedType);
        if (bookingEquipmentRepository.existsById(beId)) {
            throw new IllegalStateException("Equipment type '" + normalizedType + "' is already required for this booking.");
        }

        // 4. Create and save
        BookingEquipment newBe = new BookingEquipment();
        newBe.setBlock(bookingId.getBlock());
        newBe.setRoom(bookingId.getRoomNo()); // Ensure field name matches BookingEquipment entity ('room')
        newBe.setDateTime(bookingId.getDateTime());
        newBe.setEquipmentType(normalizedType);
        // Setting Booking/Equipment references is optional if using insertable/updatable=false

        return bookingEquipmentRepository.save(newBe);
    }

    @Transactional
    public boolean removeEquipmentFromBooking(BookingId bookingId, String equipmentType) {
        // Check if booking exists first for a clearer error,
        if (!bookingRepository.existsById(bookingId)) {
            throw new NoSuchElementException("Booking with ID " + bookingId + " not found.");
        }

        String normalizedType = equipmentType.trim().toUpperCase();
        BookingEquipmentId beId = new BookingEquipmentId(bookingId.getBlock(), bookingId.getRoomNo(), bookingId.getDateTime(), normalizedType);

        if (bookingEquipmentRepository.existsById(beId)) {
            bookingEquipmentRepository.deleteById(beId);
            return true;
        }
        return false; // Requirement did not exist
    }
}
