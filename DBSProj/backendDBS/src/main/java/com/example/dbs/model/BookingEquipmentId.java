package com.example.dbs.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class BookingEquipmentId implements Serializable {
    private String block;
    private String room;
    private LocalDateTime dateTime;
    private String equipmentType;

    public BookingEquipmentId() {}

    public BookingEquipmentId(String block, String room, LocalDateTime dateTime, String equipmentType) {
        this.block = block;
        this.room = room;
        this.dateTime = dateTime;
        this.equipmentType = equipmentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingEquipmentId)) return false;
        BookingEquipmentId that = (BookingEquipmentId) o;
        return Objects.equals(block, that.block) &&
               Objects.equals(room, that.room) &&
               Objects.equals(dateTime, that.dateTime) &&
               Objects.equals(equipmentType, that.equipmentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, room, dateTime, equipmentType);
    }
}
