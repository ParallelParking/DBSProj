package com.example.dbs.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@IdClass(BookingId.class)
public class Booking {

    @Id
    private String block;

    @Id
    @Column(name = "room_no") // ensures the DB column matches Room's field
    private String roomNo;

    @Id
    @Column(name = "date_time") // explicitly specify column name
    private LocalDateTime dateTime;

    private String purpose;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "block", referencedColumnName = "block", insertable = false, updatable = false),
        @JoinColumn(name = "room_no", referencedColumnName = "room", insertable = false, updatable = false)
    })
    private Room room;

    @ManyToOne
    @JoinColumn(name = "student_email", referencedColumnName = "email", insertable = false, updatable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "club_name", referencedColumnName = "name", insertable = false, updatable = false)
    private Club club;

    // Constructors

    public Booking() {}

    public Booking(String block, String roomNo, LocalDateTime dateTime, String purpose
        , Room room, Student student, Club club) {
        this.block = block;
        this.roomNo = roomNo;
        this.dateTime = dateTime;
        this.purpose = purpose;
        this.room = room;
        this.student = student;
        this.club = club;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }
}
