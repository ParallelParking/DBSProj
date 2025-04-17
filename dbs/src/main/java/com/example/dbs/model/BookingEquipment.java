package com.example.dbs.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@IdClass(BookingEquipmentId.class)
public class BookingEquipment {

    @Id
    private String block;

    @Id
    private String room;

    @Id
    private LocalDateTime dateTime;

    @Id
    private String equipmentType;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "block", referencedColumnName = "block", insertable = false, updatable = false),
        @JoinColumn(name = "room", referencedColumnName = "room_no", insertable = false, updatable = false),
        @JoinColumn(name = "date_time", referencedColumnName = "date_time", insertable = false, updatable = false)
    })
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "equipmentType", referencedColumnName = "type", insertable = false, updatable = false)
    private Equipment equipment;

    public BookingEquipment() {}

    public BookingEquipment(String block, String room, LocalDateTime dateTime, String equipmentType) {
        this.block = block;
        this.room = room;
        this.dateTime = dateTime;
        this.equipmentType = equipmentType;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    // Getters and setters...
}
