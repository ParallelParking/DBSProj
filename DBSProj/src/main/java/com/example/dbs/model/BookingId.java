package com.example.dbs.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;



public class BookingId implements Serializable {
    private String block;
    private String roomNo;
    private LocalDateTime dateTime;

    public BookingId() {}

    public BookingId(String block, String roomNo, LocalDateTime dateTime) {
        this.block = block;
        this.roomNo = roomNo;
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingId)) return false;
        BookingId that = (BookingId) o;
        return Objects.equals(block, that.block) &&
               Objects.equals(roomNo, that.roomNo) &&
               Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, roomNo, dateTime);
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

    // Getters and Setters
}
