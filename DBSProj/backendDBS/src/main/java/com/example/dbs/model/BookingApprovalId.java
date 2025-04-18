package com.example.dbs.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class BookingApprovalId implements Serializable {
    private String block;
    private String roomNo;
    private LocalDateTime dateTime;

    public BookingApprovalId() {}

    public BookingApprovalId(String block, String roomNo, LocalDateTime dateTime) {
        this.block = block;
        this.roomNo = roomNo;
        this.dateTime = dateTime;
    }

    // equals() and hashCode() are mandatory for composite keys

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingApprovalId)) return false;
        BookingApprovalId that = (BookingApprovalId) o;
        return block.equals(that.block) &&
               roomNo.equals(that.roomNo) &&
               dateTime.equals(that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, roomNo, dateTime);
    }
}
