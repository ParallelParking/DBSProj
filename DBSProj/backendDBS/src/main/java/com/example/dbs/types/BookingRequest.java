package com.example.dbs.types;

import java.time.LocalDateTime;

// DTO for creating a booking, including required equipment
public class BookingRequest {
    private String block;
    private String roomNo;
    private LocalDateTime dateTime;
    private String purpose;
    private String studentEmail;
    private String clubName; // Optional

    public BookingRequest() {}

    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }
}
