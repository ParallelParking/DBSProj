package com.example.dbs.model;

import java.io.Serializable;
import java.util.Objects;

public class RoomEquipmentId implements Serializable {
    private String block;
    private String room;
    private String type;

    public RoomEquipmentId() {}

    public RoomEquipmentId(String block, String room, String type) {
        this.block = block;
        this.room = room;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomEquipmentId)) return false;
        RoomEquipmentId that = (RoomEquipmentId) o;
        return Objects.equals(block, that.block) &&
               Objects.equals(room, that.room) &&
               Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, room, type);
    }
}
