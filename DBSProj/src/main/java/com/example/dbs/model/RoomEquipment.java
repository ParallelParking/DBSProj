package com.example.dbs.model;

import jakarta.persistence.*;

@Entity
@IdClass(RoomEquipmentId.class)
public class RoomEquipment {

    @Id
    private String block;

    @Id
    private String room;

    @Id
    private String type; // FK to Equipment

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "block", referencedColumnName = "block", insertable = false, updatable = false),
        @JoinColumn(name = "room", referencedColumnName = "room", insertable = false, updatable = false)
    })
    private Room roomRef;

    @ManyToOne
    @JoinColumn(name = "type", referencedColumnName = "type", insertable = false, updatable = false)
    private Equipment equipment;

    public RoomEquipment() {
    }

    public RoomEquipment(String block, String room, String type) {
        this.block = block;
        this.room = room;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Room getRoomRef() {
        return roomRef;
    }

    public void setRoomRef(Room roomRef) {
        this.roomRef = roomRef;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    // Getters and setters...
}
