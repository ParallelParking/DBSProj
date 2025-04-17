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

    // Getters and setters...
}
