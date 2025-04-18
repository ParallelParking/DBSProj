package com.example.dbs.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@IdClass(BookingApprovalId.class)
public class BookingApproval {

    @Id
    private String block;

    @Id
    @Column(name = "room_no") 
    private String roomNo;

    @Id
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    private String approverType;
    private String approverEmail;
    private String approvalStatus;
    private LocalDateTime approvalTime;
    private String comments;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "block", referencedColumnName = "block", insertable = false, updatable = false),
        @JoinColumn(name = "room_no", referencedColumnName = "room_no", insertable = false, updatable = false),
        @JoinColumn(name = "date_time", referencedColumnName = "date_time", insertable = false, updatable = false)
    })
    private Booking booking;

    public BookingApproval() {}

    public BookingApproval(String block, String roomNo, LocalDateTime dateTime,
                           String approverType, String approverEmail, String approvalStatus,
                           LocalDateTime approvalTime, String comments, Booking booking) {
        this.block = block;
        this.roomNo = roomNo;
        this.dateTime = dateTime;
        this.approverType = approverType;
        this.approverEmail = approverEmail;
        this.approvalStatus = approvalStatus;
        this.approvalTime = approvalTime;
        this.comments = comments;
        this.booking = booking;
    }

    // Getters and setters

    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getApproverType() { return approverType; }
    public void setApproverType(String approverType) { this.approverType = approverType; }

    public String getApproverEmail() { return approverEmail; }
    public void setApproverEmail(String approverEmail) { this.approverEmail = approverEmail; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public LocalDateTime getApprovalTime() { return approvalTime; }
    public void setApprovalTime(LocalDateTime approvalTime) { this.approvalTime = approvalTime; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
}
