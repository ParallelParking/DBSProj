package com.example.dbs.model;

import java.time.LocalDateTime;

import com.example.dbs.types.ApprovalStatus;
import com.example.dbs.types.ApproverRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;

@Entity
// @IdClass(BookingApprovalId.class)
public class BookingApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ensures these are set via the booking relationship
    @Column(name = "block", insertable = false, updatable = false)
    private String block;
    @Column(name = "room_no", insertable = false, updatable = false)
    private String roomNo;
    @Column(name = "date_time", insertable = false, updatable = false)
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private ApproverRole approverRole; // Changed from approverType

    private String approverEmail;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus; // Changed from String
    
    private LocalDateTime approvalTime;
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumns({
        @JoinColumn(name = "block", referencedColumnName = "block"),
        @JoinColumn(name = "room_no", referencedColumnName = "roomNo"), 
        @JoinColumn(name = "date_time", referencedColumnName = "dateTime") 
    })
    private Booking booking;

    public BookingApproval() {}

    public BookingApproval(Booking booking, ApproverRole approverRole, String approverEmail, ApprovalStatus approvalStatus, LocalDateTime approvalTime, String comments) {
        this.booking = booking;
        this.block = booking.getBlock();
        this.roomNo = booking.getRoomNo();
        this.dateTime = booking.getDateTime();
        this.approverRole = approverRole;
        this.approverEmail = approverEmail;
        this.approvalStatus = approvalStatus;
        this.approvalTime = approvalTime;
        this.comments = comments;
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public ApproverRole getApproverRole() { return approverRole; }
    public void setApproverRole(ApproverRole approverRole) { this.approverRole = approverRole; }

    public String getApproverEmail() { return approverEmail; }
    public void setApproverEmail(String approverEmail) { this.approverEmail = approverEmail; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public LocalDateTime getApprovalTime() { return approvalTime; }
    public void setApprovalTime(LocalDateTime approvalTime) { this.approvalTime = approvalTime; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
}
