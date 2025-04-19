package com.example.dbs.service;

import java.time.LocalDateTime;
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
import com.example.dbs.model.BookingId;
import com.example.dbs.model.Professor;
import com.example.dbs.model.Room;
import com.example.dbs.model.RoomId;
import com.example.dbs.repository.BookingApprovalRepository; // For atomic operations
import com.example.dbs.repository.BookingRepository;
import com.example.dbs.repository.ClubRepository;
import com.example.dbs.repository.FloorManagerRepository;
import com.example.dbs.repository.ProfessorRepository;
import com.example.dbs.repository.RoomRepository;
import com.example.dbs.repository.SecurityRepository;
import com.example.dbs.repository.StudentCouncilRepository;
import com.example.dbs.repository.StudentRepository;
import com.example.dbs.types.ApprovalStatus;
import com.example.dbs.types.ApproverRole;
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
    public Booking createBooking(Booking booking) {
        if (!roomRepository.existsById(new RoomId(booking.getBlock(), booking.getRoomNo()))) {
             throw new IllegalArgumentException("Room does not exist.");
        }
        if (!studentRepository.existsById(booking.getStudentEmail())) {
              throw new IllegalArgumentException("Student does not exist.");
        }
        if (booking.getClubName() != null && !booking.getClubName().isEmpty() && !clubRepository.existsById(booking.getClubName())) {
             throw new IllegalArgumentException("Club does not exist.");
        }
        if (bookingRepository.existsById(new BookingId(booking.getBlock(), booking.getRoomNo(), booking.getDateTime()))) {
             throw new IllegalStateException("Booking conflict exists.");
        }

        booking.setOverallStatus(BookingStatus.PENDING_APPROVAL);

        return bookingRepository.save(booking);
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
        if (booking.getClub() != null && booking.getClub().getFacultyHead() != null && booking.getClub().getFacultyHead().equals(approverEmail)) {
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
        Optional<Booking> existingBookingOpt = bookingRepository.findById(id);
        if (existingBookingOpt.isPresent()) {
            Booking existingBooking = existingBookingOpt.get();
            existingBooking.setPurpose(newPurpose);
            // Update other non-key fields as needed
            return Optional.of(bookingRepository.save(existingBooking));
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public boolean deleteBooking(BookingId id) {
        if (bookingRepository.existsById(id)) {
            // Consider cascading deletes or manual deletion of related entities
            // (BookingApproval, BookingEquipment) if not handled by JPA cascades.
            bookingRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
