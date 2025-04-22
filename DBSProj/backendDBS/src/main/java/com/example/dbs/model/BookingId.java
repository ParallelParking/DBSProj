package com.example.dbs.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;



public class BookingId implements Serializable {
    @Column(name = "block_id", nullable = false)
    private String block;

    @Column(name = "room_no", nullable = false)
    private String roomNo;

    // *** CHANGE: Renamed from startTime to startTime ***
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    public BookingId() {}

    public BookingId(String block, String roomNo, LocalDateTime startTime) {
        this.block = block;
        this.roomNo = roomNo;
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingId)) return false;
        BookingId that = (BookingId) o;
        return Objects.equals(block, that.block) &&
               Objects.equals(roomNo, that.roomNo) &&
               Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, roomNo, startTime);
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

}
